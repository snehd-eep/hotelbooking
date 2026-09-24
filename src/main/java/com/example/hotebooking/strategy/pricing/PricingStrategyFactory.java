package com.example.hotebooking.strategy.pricing;

public class PricingStrategyFactory {
    public static PricingStrategy getStrategy(PricingStrategyType type) {
         switch (type) {
             case DYNAMIC: return new DynamicPricingStrategy();
             default:  return new BasePricingStrategy();
        }
    }
}

