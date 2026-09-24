package com.example.hotebooking.strategy.payment;

import com.example.hotebooking.entity.payment.PaymentMethod;
import com.example.hotebooking.entity.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentStrategyTest {

    @Test
    void testCardPaymentStrategy() {
        CardPaymentStrategy strategy = new CardPaymentStrategy();
        assertEquals(PaymentMethod.CARD, strategy.supportedMethod());
        assertEquals(PaymentStatus.CAPTURED, strategy.pay(1000, "b1"));
    }

    @Test
    void testUpiPaymentStrategy() {
        UpiPaymentStrategy strategy = new UpiPaymentStrategy();
        assertEquals(PaymentMethod.UPI, strategy.supportedMethod());
        assertEquals(PaymentStatus.CAPTURED, strategy.pay(1000, "b1"));
    }

    @Test
    void testWalletPaymentStrategy() {
        WalletPaymentStrategy strategy = new WalletPaymentStrategy();
        assertEquals(PaymentMethod.WALLET, strategy.supportedMethod());
        assertEquals(PaymentStatus.CAPTURED, strategy.pay(1000, "b1"));
    }
}
