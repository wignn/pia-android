pub mod conflator;
pub mod indicators;
pub mod ringbuffer;

use conflator::{MarketConflator, RawTick};
use indicators::{Ema, Rsi};
use jni::objects::{JClass, JDoubleArray, JString};
use jni::sys::{jdouble, jdoubleArray, jint, jlong, jstring};
use jni::JNIEnv;
use parking_lot::Mutex;
use std::collections::HashMap;
use std::sync::OnceLock;

static CONFLATOR: OnceLock<Mutex<MarketConflator>> = OnceLock::new();
static EMA_ENGINES: OnceLock<Mutex<HashMap<usize, Ema>>> = OnceLock::new();
static RSI_ENGINES: OnceLock<Mutex<HashMap<usize, Rsi>>> = OnceLock::new();

fn get_conflator() -> &'static Mutex<MarketConflator> {
    CONFLATOR.get_or_init(|| Mutex::new(MarketConflator::new(60)))
}

fn get_ema_engines() -> &'static Mutex<HashMap<usize, Ema>> {
    EMA_ENGINES.get_or_init(|| Mutex::new(HashMap::new()))
}

fn get_rsi_engines() -> &'static Mutex<HashMap<usize, Rsi>> {
    RSI_ENGINES.get_or_init(|| Mutex::new(HashMap::new()))
}

#[no_mangle]
pub extern "system" fn Java_dev_wign_pia_data_NativeBridge_initConflator(
    _env: JNIEnv,
    _class: JClass,
    timeframe_sec: jlong,
) {
    let mut c = get_conflator().lock();
    c.set_timeframe(timeframe_sec);
}

#[no_mangle]
pub extern "system" fn Java_dev_wign_pia_data_NativeBridge_processTick(
    mut env: JNIEnv,
    _class: JClass,
    symbol: JString,
    price: jdouble,
    volume: jdouble,
    timestamp_ms: jlong,
) -> jstring {
    let sym_str: String = match env.get_string(&symbol) {
        Ok(s) => s.into(),
        Err(_) => String::from("UNKNOWN"),
    };

    let tick = RawTick {
        symbol: sym_str,
        price,
        volume,
        timestamp_ms,
    };

    let candle = {
        let mut c = get_conflator().lock();
        c.process_tick(tick)
    };

    let json = serde_json::to_string(&candle).unwrap_or_else(|_| "{}".to_string());
    match env.new_string(json) {
        Ok(s) => s.into_raw(),
        Err(_) => std::ptr::null_mut(),
    }
}

#[no_mangle]
pub extern "system" fn Java_dev_wign_pia_data_NativeBridge_calculateEma(
    _env: JNIEnv,
    _class: JClass,
    period: jint,
    price: jdouble,
) -> jdouble {
    let mut engines = get_ema_engines().lock();
    let ema = engines
        .entry(period as usize)
        .or_insert_with(|| Ema::new(period as usize));
    ema.update(price)
}

#[no_mangle]
pub extern "system" fn Java_dev_wign_pia_data_NativeBridge_calculateBatchEma(
    env: JNIEnv,
    _class: JClass,
    period: jint,
    prices: JDoubleArray,
) -> jdoubleArray {
    let len = match env.get_array_length(&prices) {
        Ok(l) => l as usize,
        Err(_) => return std::ptr::null_mut(),
    };

    let mut buf = vec![0.0f64; len];
    if env.get_double_array_region(&prices, 0, &mut buf).is_err() {
        return std::ptr::null_mut();
    }

    let calculated = Ema::calculate_series(&buf, period as usize);
    match env.new_double_array(calculated.len() as i32) {
        Ok(out_arr) => {
            if env
                .set_double_array_region(&out_arr, 0, &calculated)
                .is_err()
            {
                return std::ptr::null_mut();
            }
            out_arr.into_raw()
        }
        Err(_) => std::ptr::null_mut(),
    }
}

#[no_mangle]
pub extern "system" fn Java_dev_wign_pia_data_NativeBridge_calculateRsi(
    _env: JNIEnv,
    _class: JClass,
    period: jint,
    price: jdouble,
) -> jdouble {
    let mut engines = get_rsi_engines().lock();
    let rsi = engines
        .entry(period as usize)
        .or_insert_with(|| Rsi::new(period as usize));
    rsi.update(price).unwrap_or(-1.0)
}
