use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct RawTick {
    pub symbol: String,
    pub price: f64,
    pub volume: f64,
    pub timestamp_ms: i64,
}

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq)]
pub struct CoalescedCandle {
    pub symbol: String,
    pub time: i64,
    pub open: f64,
    pub high: f64,
    pub low: f64,
    pub close: f64,
    pub volume: f64,
}

pub struct MarketConflator {
    active_candles: HashMap<String, CoalescedCandle>,
    window_sec: i64,
}

impl MarketConflator {
    pub fn new(timeframe_sec: i64) -> Self {
        Self {
            active_candles: HashMap::new(),
            window_sec: timeframe_sec.max(1),
        }
    }

    pub fn set_timeframe(&mut self, timeframe_sec: i64) {
        self.window_sec = timeframe_sec.max(1);
        self.active_candles.clear();
    }

    pub fn process_tick(&mut self, tick: RawTick) -> CoalescedCandle {
        let bucket_time = (tick.timestamp_ms / 1000) / self.window_sec * self.window_sec;

        match self.active_candles.get_mut(&tick.symbol) {
            Some(entry) if entry.time == bucket_time => {
                entry.high = entry.high.max(tick.price);
                entry.low = entry.low.min(tick.price);
                entry.close = tick.price;
                entry.volume += tick.volume;
                entry.clone()
            }
            _ => {
                let new_candle = CoalescedCandle {
                    symbol: tick.symbol.clone(),
                    time: bucket_time,
                    open: tick.price,
                    high: tick.price,
                    low: tick.price,
                    close: tick.price,
                    volume: tick.volume,
                };
                self.active_candles.insert(tick.symbol, new_candle.clone());
                new_candle
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_conflation_ohlc() {
        let mut conflator = MarketConflator::new(60);

        let c1 = conflator.process_tick(RawTick {
            symbol: "BBCA".to_string(),
            price: 10000.0,
            volume: 10.0,
            timestamp_ms: 1700000010000,
        });
        assert_eq!(c1.open, 10000.0);
        assert_eq!(c1.high, 10000.0);
        assert_eq!(c1.low, 10000.0);
        assert_eq!(c1.close, 10000.0);
        assert_eq!(c1.volume, 10.0);

        let c2 = conflator.process_tick(RawTick {
            symbol: "BBCA".to_string(),
            price: 10100.0,
            volume: 5.0,
            timestamp_ms: 1700000020000,
        });
        assert_eq!(c2.high, 10100.0);
        assert_eq!(c2.close, 10100.0);
        assert_eq!(c2.volume, 15.0);

        let c3 = conflator.process_tick(RawTick {
            symbol: "BBCA".to_string(),
            price: 9950.0,
            volume: 20.0,
            timestamp_ms: 1700000030000,
        });
        assert_eq!(c3.low, 9950.0);
        assert_eq!(c3.close, 9950.0);
        assert_eq!(c3.volume, 35.0);
    }
}
