package com.example.armando.marketbook;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BookActivity extends AppCompatActivity {

    //Dati
    private ArrayList<String> Commenti;
    private ArrayList<String> NomeCommenti;
    private String Descrizione;

    //View
    private Toolbar Toolbar;
    private ProgressBar ProgressBar;
    private FloatingActionButton Download;
    private FloatingActionButton InfoAutore;
    private FloatingActionButton AggiungiCommento;
    private TextView Titolo;
    private TextView Autore;
    private TextView Genere;
    private TextView TitoloBar;
    private ImageView Copertina;
    private CardView Contenitore;
    private  RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_book);
        setupToolbar();
        setupProgressBar();
        setupRecyclerView();

        //Ottengo i dati passati
        Bundle extra = getIntent().getExtras();

        if(extra!=null){
            Book libro = (Book) extra.getSerializable("LIBRO");
            HashMap<String,String> transitionName = (HashMap<String, String>) extra.getSerializable("TRANSITION_NAME");
            if(libro!=null){
                setupInfo(libro,transitionName);
                setupCopertina(libro, transitionName);
                setupCommenti(libro);
                setupInfoAutore(libro);
                setupDownload();
                setupAggiungiCommento();
            }
        }

    }

    private void setupAggiungiCommento() {
        AggiungiCommento = findViewById(R.id.aggiungiCommento);
        AggiungiCommento.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Hai premuto Aggiungi Commento" , Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setupInfoAutore(final Book libro) {
        InfoAutore = findViewById(R.id.pulsanteAutore);
        InfoAutore.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent(BookActivity.this,AutoreActivity.class);
                intent.putExtra("ID",libro.getIDAutore());
                intent.putExtra("AUTORE",Toolbar.getTransitionName());
                View statusBar = findViewById(android.R.id.statusBarBackground);
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View toolbar = findViewById(R.id.toolbar2);
                List<Pair<View, String>> pairs = new ArrayList<>();
                if (statusBar != null) {
                    pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                }
                if (navigationBar != null) {
                    pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                }
                pairs.add(Pair.create(toolbar,toolbar.getTransitionName()));
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(BookActivity.this, pairs.toArray(new Pair[pairs.size()])).toBundle();
                startActivity(intent, options);
            }

        });
    }

    private void setupCommenti(final Book libro) {
        AddItemsToRecyclerViewArrayList();
        CommentAdapter adapter = new CommentAdapter(Commenti,NomeCommenti,libro.getTrama());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @SuppressLint("ResourceType")
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if ( dy>0 && Download.isShown()) {
                    Download.hide();
                    InfoAutore.hide();
                    TitoloBar.setText(libro.getTitolo());
                    Animation in = new AlphaAnimation(0.0f, 1.0f);
                    in.setDuration(300);
                    TitoloBar.startAnimation(in);
                    AggiungiCommento.show();
                } else if( dy<0 && !Download.isShown()){
                    Download.show();
                    InfoAutore.show();
                    Animation out = new AlphaAnimation(1.0f, 0.0f);
                    out.setDuration(300);
                    out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            TitoloBar.setText(null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    TitoloBar.startAnimation(out);
                    AggiungiCommento.hide();
                }
            }
        });
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerviewBook);
        RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(RecyclerViewLayoutManager);
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
    }

    private void setupCopertina(Book libro, HashMap<String,String> transitionName) {
        Copertina = findViewById(R.id.Copertina);
        Contenitore = findViewById(R.id.cardview);
        Contenitore.setTransitionName(transitionName.get(Contenitore.getResources().getResourceName(Contenitore.getId())));
        Copertina.setTransitionName(transitionName.get(Copertina.getResources().getResourceName(Copertina.getId())));
        if(libro.getPath()==null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(libro.getURLCopertina());
            try {
                final File localFile = File.createTempFile(libro.getTitolo(), "png");
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(140 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),Math.round(190 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),true);
                        Copertina.setImageBitmap(bitmap);
                        supportStartPostponedEnterTransition();
                        ProgressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        supportStartPostponedEnterTransition();
                    }
                });
            } catch (IOException e ) {
                Copertina.setImageResource(R.drawable.ic_image_black_24dp);
            }
        }   else{
            Bitmap bitmap = BitmapFactory.decodeFile(libro.getPath());
            bitmap = Bitmap.createScaledBitmap(bitmap, 140,190,true);
            Copertina.setImageBitmap(bitmap);
            ProgressBar.setVisibility(View.GONE);
        }
    }

    private void setupDownload() {
        Download = findViewById(R.id.pulsanteDownload);
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
    }

    private void setupInfo(Book libro, HashMap<String,String> transitionName) {
        Titolo = findViewById(R.id.titololibro);
        Autore = findViewById(R.id.autoreLibro);
        Titolo.setTransitionName(transitionName.get(Titolo.getResources().getResourceName(Titolo.getId())));
        Autore.setTransitionName(transitionName.get(Autore.getResources().getResourceName(Autore.getId())));
        Genere = findViewById(R.id.genereLibro);
        Titolo.setText(libro.getTitolo());
        Autore.setText(libro.getAutore());
        Genere.setText(libro.getGenere());
    }

    private void setupProgressBar() {
        ProgressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        TitoloBar = findViewById(R.id.titoloBar);
        Toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(Toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupWindowAnimations() {
        Transition slideEntrata = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(slideEntrata);
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.autoreout);
        getWindow().setExitTransition(slide);
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