package br.edu.utfpr.vanderleyjunioralunos.vacineme.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import br.edu.utfpr.vanderleyjunioralunos.vacineme.activities.adapters.RelationshipSpinnerAdapter;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.R;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.models.Relationship;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.models.Person;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.persistence.VacinemeDatabase;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.utils.AlertsUtil;
import br.edu.utfpr.vanderleyjunioralunos.vacineme.utils.DateUtil;

public class AddNewPersonActivity extends AppCompatActivity {

    private TextView name;
    private RadioGroup genrer;
    private Spinner spinnerRelationship;
    private DatePickerDialog datePickerDialog;
    private TextView dateOfBorn;
    private Button button;
    private Calendar calendar;
    private int PERSON_ID = -1;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_person);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        name = findViewById(R.id.editTextName);
        dateOfBorn = findViewById(R.id.editTextDateOfBorn);
        genrer = findViewById(R.id.radioGroupGender);
        spinnerRelationship = findViewById(R.id.spinnerRelationship);
        button = findViewById(R.id.buttonAction);

        insertDataSpinnerRelationship();
        datePickerEvent();

        if(!verifyIntentMode(getIntent())){
            setTitle(getString(R.string.inserir_uma_nova_pessoa));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!verifyIntentMode(getIntent())){
            menu.getItem(0).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean verifyIntentMode(Intent intent){
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            setTitle(getString(R.string.edit_person));
            button.setText(R.string.save_changes);
            PERSON_ID = bundle.getInt("PERSON_ID");
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    person = VacinemeDatabase.getDatabase(AddNewPersonActivity.this).personDAO().queryForId(PERSON_ID);
                    AddNewPersonActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setValuesForm(person);
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    private void setValuesForm(Person p){
        if(p!=null){
            name.setText(p.getName());
            dateOfBorn.setText(new SimpleDateFormat(getString(R.string.formato_data)).format(p.getDateOfBorn()));
            RadioButton f = (RadioButton) genrer.getChildAt(0);
            RadioButton m = (RadioButton) genrer.getChildAt(1);
            if(p.getGender().equalsIgnoreCase(getString(R.string.genero_feminino))){
                f.setChecked(true);
            } else {
                m.setChecked(true);
            }
            spinnerRelationship.setSelection(p.getRelationship());
        } else {
            Toast.makeText(this, R.string.no_people_found, Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void datePickerEvent() {
        dateOfBorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(AddNewPersonActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int dayOfMonth) {
                        dateOfBorn.setText(DateUtil.dateFormatter(dayOfMonth, mMonth, mYear, getString(R.string.formato_data)));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void insertDataSpinnerRelationship() {
        String[] descs = getResources().getStringArray(R.array.relationship);
        TypedArray icons = getResources().obtainTypedArray(R.array.icones_parentesco);

        ArrayList<Relationship> relationships = new ArrayList();

        for (int cont = 0; cont < descs.length; cont++) {
            relationships.add(new Relationship(descs[cont], icons.getDrawable(cont)));
        }

        RelationshipSpinnerAdapter relationshipSpinnerAdapter = new RelationshipSpinnerAdapter(this, relationships);

        spinnerRelationship.setAdapter(relationshipSpinnerAdapter);
    }

    private String getSelectedGender(int id) {
        switch (id) {
            case R.id.radioButtonFemale:
                return getString(R.string.feminino);
            case R.id.radioButtonMale:
                return getString(R.string.masculino);
        }
        return "";
    }

    private void deletePerson(final Person p){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                VacinemeDatabase.getDatabase(AddNewPersonActivity.this).personDAO().delete(p);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                }
            }
        };
        AlertsUtil.confirmation(this, getString(R.string.do_you_really_want_to_delete_this_person), listener);
    }

    public void saveNewPerson(View view) {
        if (verifyForm()) {
            try {
                final Person p = new Person(
                        name.getText().toString(),
                        new SimpleDateFormat(getString(R.string.formato_data)).parse(dateOfBorn.getText().toString()),
                        getSelectedGender(genrer.getCheckedRadioButtonId()),
                        Relationship.findRelationshipPosition(((Relationship) spinnerRelationship.getSelectedItem()).getDescription(), this)
                );
                if(!button.getText().toString().equalsIgnoreCase(getString(R.string.save_changes))){
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            VacinemeDatabase.getDatabase(AddNewPersonActivity.this).personDAO().insert(p);
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                } else {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            p.setId(person.getId());
                            VacinemeDatabase.getDatabase(AddNewPersonActivity.this).personDAO().update(p);
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
            } catch (ParseException e) {
                Toast.makeText(this, R.string.verifique_a_data_de_nascimento, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean verifyForm() {
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.informe_o_nome_da_pessoa, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dateOfBorn.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.informe_a_data_de_nascimento, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (genrer.getCheckedRadioButtonId()==-1) {
            Toast.makeText(this, R.string.informe_o_genero_da_pessoa, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerRelationship.getSelectedItemPosition() == -1) {
            Toast.makeText(this, R.string.informe_o_parentesco, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuItemDelete:
                deletePerson(person);
        }
        return true;
    }
}
