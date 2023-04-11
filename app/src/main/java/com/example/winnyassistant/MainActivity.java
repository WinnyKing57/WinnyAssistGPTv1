package com.example.winnyassistant;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ListView responseListView;
    private ArrayAdapter<String> responseAdapter;

    public ArrayAdapter<String> getResponseAdapter() {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
    }

    private ChatGPT chatbot;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstStart = sharedPreferences.getBoolean("firstStart", true);

        if (isFirstStart) {
            //Afficher une boîte de dialogue pour la demande de la clé API
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clé API");
            builder.setMessage("Veuillez entrer votre clé API");

            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String key = input.getText().toString().trim();
                    //Sauvegarder la clé dans les préférences partagées
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("api_key", key);
                    editor.putBoolean("firstStart", false);
                    editor.apply();

                    //Initialiser l'agent Dialogflow
                    initAgent(key);

                    //Démarrer la conversation
                    responseListView = findViewById(R.id.response_list);
                    responseListView.setVisibility(View.VISIBLE);
                    responseAdapter = getResponseAdapter();
                    responseListView.setAdapter(responseAdapter);
                    try {
                        chatbot = new ChatGPT(key, MainActivity.this);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    chatbot.addMessage("Bonjour ! Comment puis-je vous aider ?");
                }
            });

            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

            builder.show();
        } else {
            //Initialiser l'agent Dialogflow
            String key = sharedPreferences.getString("api_key", "");
            initAgent(key) ;

            //Démarrer la conversation directement
            responseListView = findViewById(R.id.response_list);
            responseListView.setVisibility(View.VISIBLE);
            responseAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, new ArrayList<>());
            responseListView.setAdapter(responseAdapter);
            try {
                chatbot = new ChatGPT(key, MainActivity.this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chatbot.addMessage("Bonjour ! Comment puis-je vous aider ?");
        }

        final EditText inputText = findViewById(R.id.edit_text);

        // Ajout de l'écouteur de clic sur le bouton pour la reconnaissance vocale
        findViewById(R.id.speech_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Gérer la reconnaissance vocale
            }
        });
    }

    private void initAgent(String key) {
        // TODO: Initialiser l'agent Dialogflow
    }
}
