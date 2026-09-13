use std::sync::atomic::{AtomicUsize, Ordering};

pub struct TickRingBuffer<T, const CAP: usize> {
    buffer: Vec<Option<T>>,
    head: AtomicUsize,
    tail: AtomicUsize,
}

impl<T: Clone, const CAP: usize> Default for TickRingBuffer<T, CAP> {
    fn default() -> Self {
        Self::new()
    }
}

impl<T: Clone, const CAP: usize> TickRingBuffer<T, CAP> {
    pub fn new() -> Self {
        assert!(
            CAP > 0 && (CAP & (CAP - 1)) == 0,
            "Capacity must be a power of two"
        );
        let mut buffer = Vec::with_capacity(CAP);
        for _ in 0..CAP {
            buffer.push(None);
        }
        Self {
            buffer,
            head: AtomicUsize::new(0),
            tail: AtomicUsize::new(0),
        }
    }

    pub fn push(&mut self, item: T) -> bool {
        let head = self.head.load(Ordering::Relaxed);
        let tail = self.tail.load(Ordering::Acquire);

        if head.wrapping_sub(tail) >= CAP {
            let next_tail = tail.wrapping_add(1);
            self.tail.store(next_tail, Ordering::Release);
        }

        let idx = head & (CAP - 1);
        self.buffer[idx] = Some(item);
        self.head.store(head.wrapping_add(1), Ordering::Release);
        true
    }

    pub fn drain_into(&mut self, out: &mut Vec<T>) {
        let head = self.head.load(Ordering::Acquire);
        let mut tail = self.tail.load(Ordering::Relaxed);

        while tail != head {
            let idx = tail & (CAP - 1);
            if let Some(item) = self.buffer[idx].take() {
                out.push(item);
            }
            tail = tail.wrapping_add(1);
        }
        self.tail.store(tail, Ordering::Release);
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_ring_buffer_push_drain() {
        let mut rb = TickRingBuffer::<i32, 4>::new();
        rb.push(10);
        rb.push(20);

        let mut out = Vec::new();
        rb.drain_into(&mut out);
        assert_eq!(out, vec![10, 20]);
    }
}
