package nnk.translate2.ltd;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import nnk.translate2.ltd.Utils.ThemeUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.set_theme(this);

        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            SharedPreferences settings_sp = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference lp_theme, lp_engine;

            lp_theme = findPreference("theme");
            lp_engine = findPreference("engine");
            lp_theme.setValue(settings_sp.getString("theme", "blue"));
            lp_engine.setValue(settings_sp.getString("engine", "google"));
            lp_theme.setOnPreferenceChangeListener(this);
            lp_engine.setOnPreferenceChangeListener(this);
        }


        /**
         * sp值变更监听
         * @param preference sp
         * @param newValue 值
         * @return true
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SharedPreferences settings_sp = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
            switch (preference.getKey()){
                case "theme":
                    settings_sp.edit().putString("theme", (String) newValue).apply();
                    break;
                case "engine":
                    settings_sp.edit().putString("engine", (String) newValue).apply();
                    break;
            }
            return true;
        }
    }



    }
