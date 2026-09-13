#[derive(Debug, Clone)]
pub struct Ema {
    multiplier: f64,
    current: Option<f64>,
}

impl Ema {
    pub fn new(period: usize) -> Self {
        let p = period.max(1) as f64;
        let multiplier = 2.0 / (p + 1.0);
        Self {
            multiplier,
            current: None,
        }
    }

    pub fn update(&mut self, price: f64) -> f64 {
        match self.current {
            Some(prev) => {
                let next = (price - prev) * self.multiplier + prev;
                self.current = Some(next);
                next
            }
            None => {
                self.current = Some(price);
                price
            }
        }
    }

    pub fn value(&self) -> Option<f64> {
        self.current
    }

    pub fn calculate_series(prices: &[f64], period: usize) -> Vec<f64> {
        let mut ema = Ema::new(period);
        prices.iter().map(|&p| ema.update(p)).collect()
    }
}

#[derive(Debug, Clone)]
pub struct Rsi {
    period: usize,
    prev_price: Option<f64>,
    avg_gain: f64,
    avg_loss: f64,
    count: usize,
}

impl Rsi {
    pub fn new(period: usize) -> Self {
        Self {
            period: period.max(1),
            prev_price: None,
            avg_gain: 0.0,
            avg_loss: 0.0,
            count: 0,
        }
    }

    pub fn update(&mut self, price: f64) -> Option<f64> {
        let prev = match self.prev_price {
            Some(p) => p,
            None => {
                self.prev_price = Some(price);
                return None;
            }
        };

        let diff = price - prev;
        let gain = if diff > 0.0 { diff } else { 0.0 };
        let loss = if diff < 0.0 { -diff } else { 0.0 };
        self.prev_price = Some(price);

        if self.count < self.period {
            self.avg_gain += gain;
            self.avg_loss += loss;
            self.count += 1;
            if self.count == self.period {
                self.avg_gain /= self.period as f64;
                self.avg_loss /= self.period as f64;
            } else {
                return None;
            }
        } else {
            let p = self.period as f64;
            self.avg_gain = (self.avg_gain * (p - 1.0) + gain) / p;
            self.avg_loss = (self.avg_loss * (p - 1.0) + loss) / p;
        }

        if self.avg_loss == 0.0 {
            Some(100.0)
        } else {
            let rs = self.avg_gain / self.avg_loss;
            Some(100.0 - (100.0 / (1.0 + rs)))
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_ema() {
        let mut ema = Ema::new(10);
        let first = ema.update(100.0);
        assert!((first - 100.0).abs() < 1e-6);
        let second = ema.update(110.0);
        assert!(second > 100.0 && second < 110.0);
    }

    #[test]
    fn test_ema_series() {
        let prices = vec![100.0, 102.0, 105.0, 103.0];
        let series = Ema::calculate_series(&prices, 5);
        assert_eq!(series.len(), 4);
        assert!((series[0] - 100.0).abs() < 1e-6);
    }

    #[test]
    fn test_rsi() {
        let mut rsi = Rsi::new(3);
        assert_eq!(rsi.update(10.0), None);
        assert_eq!(rsi.update(12.0), None);
        assert_eq!(rsi.update(15.0), None);
        let val = rsi.update(18.0);
        assert!(val.is_some());
        assert_eq!(val.unwrap(), 100.0);
    }
}
