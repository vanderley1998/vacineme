package br.edu.utfpr.vanderleyjunioralunos.vacineme.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.vanderleyjunioralunos.vacineme.R;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.activities.adapters.VaccinesListViewAdapter;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.entities.Vaccine;

public class VaccinesActivity extends AppCompatActivity {

    private static VaccinesListViewAdapter vaccinesListViewAdapter;
    private ListView listViewVaccines;
    private int selectedPosition = -1;
    private String ITEM_POSITION = "ITEM_POSITION";
    private int EDITOR_MODE = 1;
    private View selectedView;
    ActionMode actionMode;

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            mode.setTitle(R.string.choose_an_option);
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Intent intentMenu;
            switch (item.getItemId()) {
                case R.id.menuItemDelete:
                    MainActivity.getVaccines().remove(selectedPosition);
                    vaccinesListViewAdapter.notifyDataSetChanged();
                    Toast.makeText(VaccinesActivity.this, R.string.successfully_deleted, Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                case R.id.menuItemChange:
                    intentMenu = new Intent(VaccinesActivity.this, AddNewVaccineActivity.class);
                    intentMenu.putExtra(ITEM_POSITION, selectedPosition);
                    startActivityForResult(intentMenu, EDITOR_MODE);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(selectedView != null){
                selectedView.setBackgroundColor(Color.TRANSPARENT);
            }
            actionMode = null;
            selectedView = null;
            selectedPosition = -1;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccines);
        setTitle(getString(R.string.vaccines));

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setListViewPeople();
    }

    private void setListViewPeople(){
        listViewVaccines = findViewById(R.id.listViewVaccines);
        vaccinesListViewAdapter = new VaccinesListViewAdapter(this, MainActivity.getVaccines());
        listViewVaccines.setAdapter(vaccinesListViewAdapter);

        listViewVaccines.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewVaccines.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(actionMode!=null){
                    return false;
                }
                selectedView = view;
                selectedPosition = position;
                view.setBackgroundColor(Color.LTGRAY);
                view.setSelected(true);
                actionMode = startActionMode(actionModeCallback);
                return true;
            }
        });
        listViewVaccines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentMenu;
                intentMenu = new Intent(VaccinesActivity.this, AddNewVaccineActivity.class);
                intentMenu.putExtra(ITEM_POSITION, position);
                if(actionMode!=null){
                    actionMode.finish();
                }
                startActivityForResult(intentMenu, EDITOR_MODE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intentMenu;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuItemAddNewVaccine:
                intentMenu = new Intent(this, AddNewVaccineActivity.class);
                startActivity(intentMenu);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vaccines_menu, menu);
        return true;
    }

    public static void updateListView() {
        vaccinesListViewAdapter.notifyDataSetChanged();
    }
}
