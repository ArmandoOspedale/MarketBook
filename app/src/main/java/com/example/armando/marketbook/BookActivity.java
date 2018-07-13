package com.example.armando.marketbook;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {

    private FloatingActionButton Download;
    private FloatingActionButton InfoAutore;
    private FloatingActionButton AggiungiCommento;

    //Dati di prova
    private ArrayList<String> Commenti;
    private ArrayList<String> NomeCommenti;
    private String Descrizione;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Punto alla toolbar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Aggiungo il pulsante per tornare all' Activity precedente
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                onBackPressed();
            }

        });

        // Impostosto i riferimenti alle view
        TextView titolo = findViewById(R.id.titololibro);
        TextView autore = findViewById(R.id.autoreLibro);
        Download = findViewById(R.id.pulsanteDownload);
        InfoAutore = findViewById(R.id.pulsanteAutore);
        AggiungiCommento = findViewById(R.id.aggiungiCommento);

        //Ottengo i dati passati
        Intent intent = getIntent();
        final Items libro = (Items)intent.getSerializableExtra("Items");

        if(libro!=null){

            // Ottengo il riferimento alla RecyclerView per la descrizione e i commenti
            final RecyclerView mRecyclerView = findViewById(R.id.recyclerviewBook);

            // Per la RecyclerView necessita di un LayoutManager
            //Creo un istanza di  LinearLayoutManager da associare alla RecyclerView
            RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(RecyclerViewLayoutManager);

            // Aggiungo gli oggetti alla RecyclerView.
            AddItemsToRecyclerViewArrayList();

            // Creo l'adapter e lo associo alla RecyclerView
            CommentAdapter adapter = new CommentAdapter(Commenti,NomeCommenti,Descrizione);
            mRecyclerView.setAdapter(adapter);

            //Setto Titolo e Autore
            titolo.setText(libro.getTitolo());
            autore.setText(libro.getAutore());
            //genere.setText(libro.getGenere());

            //Azione legata alla pressione del pulsante
            Download.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {

                    // Inizializzo una nuova istanza del LayoutInflater service
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    // Creo la mia customView infilandoci il layout desiderato
                    View customView = inflater.inflate(R.layout.activity_barcode,null);
                    // Associo una animazione
                    customView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.animator.popupanim));
                    // Creo un popupwindows che mostra la mia customView
                    PopupWindow mPopupWindow = new PopupWindow(customView);
                    mPopupWindow.setWidth(800);
                    mPopupWindow.setHeight(350);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setElevation(8);
                    TextView descrizioneAcquisto = customView.findViewById(R.id.messaggio);
                    Button acquista = customView.findViewById(R.id.acquista_button);
                    descrizioneAcquisto.setText("Se premi ACQUISTA accetti di acquistare questo libro e di procedere quindi alla procedura di pagamento");
                    mPopupWindow.showAtLocation(Download, Gravity.CENTER, 0 , 0);
                    mPopupWindow.setOutsideTouchable(false);

                    acquista.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(),"Avviata procedura di acquisto" , Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            });

            InfoAutore.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    //Toast.makeText(v.getContext(),"Hai premuto Info Autore" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(),AutoreActivity.class);
                    intent.putExtra("Items",libro.getAutore());
                    startActivity(intent);

                }

            });

            AggiungiCommento.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"Hai premuto Aggiungi Commento" , Toast.LENGTH_SHORT).show();
                }

            });

            // Azione legata allo scroll dei commenti che nascondo il tasto flootante
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                    if ( dy>0 && Download.isShown()) {
                        Download.hide();
                        InfoAutore.hide();
                        AggiungiCommento.show();
                    } else if( dy<0 && !Download.isShown()){
                        Download.show();
                        InfoAutore.show();
                        AggiungiCommento.hide();
                    }
                }



            });

        }

    }

    // Funzione di prova per popolare la pagina
    public void AddItemsToRecyclerViewArrayList(){

        Commenti= new ArrayList<>();
        NomeCommenti = new ArrayList<>();
        NomeCommenti.add("NomeCommento1");
        Commenti.add("Commento1");
        NomeCommenti.add("NomeCommento2");
        Commenti.add("Commento2");
        NomeCommenti.add("NomeCommento3");
        Commenti.add("Commento3");
        NomeCommenti.add("NomeCommento4");
        Commenti.add("Commento4");
        NomeCommenti.add("NomeCommento5");
        Commenti.add("Commento5");
        NomeCommenti.add("NomeCommento6");
        Commenti.add("Commento6");
        NomeCommenti.add("NomeCommento7");
        Commenti.add("Commento7");
        NomeCommenti.add("NomeCommento8");
        Commenti.add("Commento8");
        NomeCommenti.add("NomeCommento1");
        Commenti.add("Commento1");
        NomeCommenti.add("NomeCommento2");
        Commenti.add("Commento2");
        NomeCommenti.add("NomeCommento3");
        Commenti.add("Commento3");
        NomeCommenti.add("NomeCommento4");
        Commenti.add("Commento4");
        NomeCommenti.add("NomeCommento5");
        Commenti.add("Commento5");
        NomeCommenti.add("NomeCommento6");
        Commenti.add("Commento6");
        NomeCommenti.add("NomeCommento7");
        Commenti.add("Commento7");
        NomeCommenti.add("NomeCommento8");
        Commenti.add("Commento8");
        Descrizione="aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    }





}