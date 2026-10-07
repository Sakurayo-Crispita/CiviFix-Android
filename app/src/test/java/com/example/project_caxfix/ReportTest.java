package com.example.project_caxfix;
import org.junit.Test;
import static org.junit.Assert.*;
public class ReportTest {
    private Report report(long age) {
        return new Report(1, 1, "Vecino", "Centro", "Pendiente", "Bache", "Baches y Pistas", "Descripción", null,
            System.currentTimeMillis() - age, 0, 0, false);
    }
    @Test public void futureTimeDoesNotProduceNegativeAge() { assertEquals("Hace un momento", report(-600000).getFormattedTimeAgo()); }
    @Test public void pastTimesUseCorrectUnit() {
        assertEquals("Hace 10 min", report(600000).getFormattedTimeAgo());
        assertEquals("Hace 2 h", report(7200000).getFormattedTimeAgo());
        assertEquals("Hace 2 d", report(172800000).getFormattedTimeAgo());
    }
}
