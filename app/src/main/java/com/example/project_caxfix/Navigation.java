package com.example.project_caxfix;
import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public final class Navigation {
    private Navigation() {}
    public static void bind(Activity activity, BottomNavigationView nav, int current) {
        nav.setItemActiveIndicatorEnabled(false);
        nav.setSelectedItemId(current);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_new) { showNewReport(activity); return false; }
            if (item.getItemId() == current) return true;
            Class<?> target = item.getItemId() == R.id.nav_profile ? ProfileActivity.class :
                item.getItemId() == R.id.nav_activity ? ActivityActivity.class : HomeActivity.class;
            activity.startActivity(new Intent(activity, target).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            if (!(activity.getClass() == HomeActivity.class)) activity.finish();
            return true;
        });
    }
    public static void showNewReport(Activity activity) {
        if (activity instanceof androidx.fragment.app.FragmentActivity) {
            androidx.fragment.app.FragmentManager manager = ((androidx.fragment.app.FragmentActivity) activity).getSupportFragmentManager();
            if (manager.findFragmentByTag(NewReportBottomSheet.TAG) == null)
                new NewReportBottomSheet().show(manager, NewReportBottomSheet.TAG);
        }
    }
}
