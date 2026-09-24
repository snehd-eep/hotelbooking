package com.example.hotebooking.strategy.cancellation;

public class CancellationPolicyFactory {
    public static CancellationPolicy getPolicy(CancellationPolicyType type) {
          switch (type) {
             case PARTIAL_REFUND: {
                 return new PartialRefundPolicy();
             }

              default: return new FreeCancellationPolicy();
          }

    }
}

