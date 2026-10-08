package com.example.project_caxfix;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.project_caxfix.data.UserDao;
import com.example.project_caxfix.data.ReportDao;
import com.example.project_caxfix.data.CiviFixDatabaseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocalFlowTest {
    @Test public void accountReportSupportCommentAndOwnershipFlow() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserDao users = new UserDao(context); ReportDao reports = new ReportDao(context);
        String suffix = java.util.UUID.randomUUID().toString();
        String dni = String.format(java.util.Locale.ROOT, "%08d", new java.security.SecureRandom().nextInt(100000000));
        long userId = -1, secondId = -1, reportId = -1, anonymousId = -1;
        try {
            userId = users.register("Vecino Prueba", suffix + "@example.com", dni, "Centro", "Prueba123!");
            assertTrue(userId > 0);
            assertEquals(-1, users.register("Duplicado", suffix + "@example.com", dni, "Centro", "Prueba123!"));
            assertNull(users.login(dni, "Incorrecta"));
            User user = users.login(dni, "Prueba123!"); assertNotNull(user); assertEquals(userId, user.id);
            assertNotNull(users.login((suffix + "@example.com").toUpperCase(java.util.Locale.ROOT), "Prueba123!"));
            reportId = reports.insert(user, "Bache en la calle", "Baches y Pistas", "Descripción del problema", null);
            assertNotNull(reports.get(reportId, userId));
            assertTrue(reports.getAll(userId, true).stream().anyMatch(r -> r.title.equals("Bache en la calle")));
            reports.toggleSupport(reportId, userId);
            assertTrue(reports.get(reportId, userId).supported); assertEquals(1, reports.get(reportId, userId).supportCount);
            reports.toggleSupport(reportId, userId);
            assertFalse(reports.get(reportId, userId).supported); assertEquals(0, reports.get(reportId, userId).supportCount);
            reports.addComment(reportId, userId, "  Se necesita atención.  ");
            assertEquals(1, reports.get(reportId, userId).commentCount);
            assertEquals("Se necesita atención.", reports.comments(reportId).get(0).body);
            anonymousId = reports.insert(user, "Reporte anónimo", "Baches y Pistas", "Evidencia del problema en la vía", null, true);
            assertEquals("Vecino anónimo", reports.get(anonymousId, userId).authorName);
            assertTrue(users.update(userId, "Nombre actualizado", "Belén"));
            assertEquals("Nombre actualizado", reports.get(reportId, userId).authorName);
            assertEquals("Vecino anónimo", reports.get(anonymousId, userId).authorName);
            assertTrue(reports.deleteOwned(anonymousId, userId));
            String secondDni = String.format(java.util.Locale.ROOT, "%08d", (Long.parseLong(dni) + 1) % 100000000);
            secondId = users.register("Otro Vecino", suffix + "2@example.com", secondDni, "Centro", "Prueba123!");
            assertTrue(secondId > 0); assertFalse(reports.deleteOwned(reportId, secondId));
            assertTrue(reports.deleteOwned(reportId, userId));
            assertNull(reports.get(reportId, userId)); assertTrue(reports.comments(reportId).isEmpty());
        } finally {
            SQLiteDatabase db = CiviFixDatabaseHelper.get(context).getWritableDatabase();
            if (reportId > 0 && userId > 0) reports.deleteOwned(reportId, userId);
            if (anonymousId > 0 && userId > 0) reports.deleteOwned(anonymousId, userId);
            if (userId > 0) db.delete("users", "id = ?", new String[]{String.valueOf(userId)});
            if (secondId > 0) db.delete("users", "id = ?", new String[]{String.valueOf(secondId)});
        }
    }
}
