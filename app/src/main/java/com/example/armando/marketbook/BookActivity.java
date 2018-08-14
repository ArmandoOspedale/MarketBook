package com.example.armando.marketbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BookActivity extends AppCompatActivity {

    //View
    private Toolbar Toolbar;
    private ProgressBar ProgressBar;
    private ProgressBar CaricamentoCommenti;
    private FloatingActionButton Download;
    private FloatingActionButton InfoAutore;
    private TextView TitoloBar;
    private ImageView Copertina;
    private RecyclerView mRecyclerView;

    //Database
    private DocumentReference reference;

    //Dati
    private Book libro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_book);

        //Ottengo i dati passati
        Bundle extra = getIntent().getExtras();
        if(extra!=null){
            setupToolbar();
            setupProgressBar();
            setupRecyclerView();
            libro = (Book) extra.getSerializable("LIBRO");
            HashMap<String,String> transitionName = (HashMap<String, String>) extra.getSerializable("TRANSITION_NAME");
            if(libro!=null && transitionName!=null){
                setupCopertina(transitionName);
                setupInfo(transitionName);
                setupInfoAutore();
                setupDownload();
                setupAggiungiCommento(libro.getRiferimento());
                ricercaCommenti(false);
            }
        }

    }

    private void ricercaCommenti(final Boolean newData) {
        final List <Commento> commenti = new ArrayList<>();
        reference = FirebaseFirestore.getInstance().document(libro.getRiferimento());
        reference.collection("Commenti").orderBy("Data", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Commento commento = documentSnapshot.toObject(Commento.class);
                        commenti.add(commento);
                    }
                    setupCommenti(commenti,libro.getTrama(),libro.getTitolo(),newData);
                }
            }
        });
    }

    private void setupAggiungiCommento(final String reference) {
        FloatingActionButton aggiungiCommento = findViewById(R.id.aggiungiCommento);
        aggiungiCommento.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                final ImageView sfondoSfocato = findViewById(R.id.sfocato);
                Bitmap map=takeScreenShot(BookActivity.this);
                Bitmap fast=fastblur(map);
                Drawable draw=new BitmapDrawable(getResources(),fast);
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View inflatedLayoutView = layoutInflater != null ? layoutInflater.inflate(R.layout.popup_commento, null) : null;
                if (inflatedLayoutView != null) {
                    inflatedLayoutView.setAnimation(AnimationUtils.loadAnimation(BookActivity.this, R.anim.popup_enter));
                    final PopupWindow popupWindow = new PopupWindow(inflatedLayoutView);
                    sfondoSfocato.setImageDrawable(draw);
                    sfondoSfocato.setVisibility(View.VISIBLE);
                    popupWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setOutsideTouchable(false);
                    popupWindow.setFocusable(true);
                    popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0 , 0);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            sfondoSfocato.setVisibility(View.GONE);
                        }
                    });
                    setupPulsanti(inflatedLayoutView, popupWindow,reference ,sfondoSfocato);
                }
            }

        });
    }

    private void setupPulsanti(final View view, final PopupWindow popupWindow, final String reference ,final ImageView sfondoSfocato) {
        Button procedi = view.findViewById(R.id.pubblica);
        ImageButton annulla = view.findViewById(R.id.annulla);
        final EditText Commento = view.findViewById(R.id.Commento_edittext);
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        procedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(Commento)){
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                    popupWindow.dismiss();
                    makeSendComment(Commento.getText().toString(), reference);
                }else{
                    Commento.setHint("Ops!" + "😅 \r\n" + "Hai dimenticato di inserire il tuo commento" );
                    Commento.setHintTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                popupWindow.dismiss();
                Toast.makeText(v.getContext(),"Commento annullato" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeSendComment(String commento , String path) {
        Map<String, Object> dati = new HashMap<>();
        dati.put("Commento", commento);
        dati.put("Data", new Date());
        dati.put("NomeUtente", "Anonimo");
        reference.collection("Commenti").document().set(dati).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mRecyclerView.setVisibility(View.GONE);
                CaricamentoCommenti.setVisibility(View.VISIBLE);
                ricercaCommenti(true);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Errore nell'aggiungere il commento" , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() > 0;
    }

    private Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    private Bitmap fastblur(Bitmap sentBitmap) {

        Bitmap old = sentBitmap;
        int width = Math.round(sentBitmap.getWidth() * 0.2f);
        int height = Math.round(sentBitmap.getHeight() * 0.2f);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (6 < 1) { return (null); }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = 6 + 6 + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) { dv[i] = (i / divsum); }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = 6 + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -6; i <= 6; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + 6];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = 6;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - 6 + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) { vmin[x] = Math.min(x + 6 + 1, wm); }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -6 * w;
            for (i = -6; i <= 6; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + 6];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = 6;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - 6 + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) { vmin[y] = Math.min(y + r1, hm) * w; }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        bitmap = Bitmap.createScaledBitmap(bitmap, old.getWidth(), old.getHeight(), false);
        return (bitmap);
    }

    private void setupInfoAutore() {
        InfoAutore = findViewById(R.id.pulsanteAutore);
        InfoAutore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookActivity.this,AutoreActivity.class);
                intent.putExtra("ID",libro.getIDAutore());
                View statusBar = findViewById(android.R.id.statusBarBackground);
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                List<Pair<View, String>> pairs = new ArrayList<>();
                if (statusBar != null) {
                    pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                }
                if (navigationBar != null) {
                    pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                }
                if (Toolbar != null) {
                    pairs.add(Pair.create((View)Toolbar, Toolbar.getTransitionName()));
                }
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(BookActivity.this, pairs.toArray(new Pair[pairs.size()])).toBundle();
                startActivity(intent, options);
            }

        });
    }

    private void setupCommenti(List<Commento> commenti, String trama, final String titolo, boolean flag) {
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        CommentAdapter adapter = new CommentAdapter(commenti,trama, appBarLayout);
        mRecyclerView.setAdapter(adapter);
        if(!flag){
            mRecyclerView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_left));
        }else{
            mRecyclerView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_left));
            Toast.makeText(getApplicationContext(),"Commento aggiunto con successo" , Toast.LENGTH_SHORT).show();
            CaricamentoCommenti.setVisibility(View.INVISIBLE);
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    Download.hide();
                    InfoAutore.hide();
                    if(TitoloBar.getText().toString().equals("")){
                        TitoloBar.setText(titolo);
                        Animation in = new AlphaAnimation(0.0f, 1.0f);
                        in.setDuration(300);
                        TitoloBar.startAnimation(in);
                    }
                }
                else
                {
                    Download.show();
                    InfoAutore.show();
                    if(TitoloBar.getText().toString().equals(titolo)){
                        Animation out = new AlphaAnimation(1.0f, 0.0f);
                        out.setDuration(300);
                        out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation){}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                TitoloBar.setText(null);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation){}
                        });
                        TitoloBar.startAnimation(out);
                    }
                }
            }
        });
    }

    private void setupCopertina(HashMap<String,String> transitionName) {
        Copertina = findViewById(R.id.Copertina);
        CardView contenitore = findViewById(R.id.cardview);
        contenitore.setTransitionName(transitionName.get(contenitore.getResources().getResourceName(contenitore.getId())));
        Copertina.setTransitionName(transitionName.get(Copertina.getResources().getResourceName(Copertina.getId())));
        final DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        if(libro.getPath()==null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(libro.getURLCopertina());
            try {
                final File localFile = File.createTempFile(libro.getTitolo(), "png");
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(140 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),Math.round(190 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),true);
                        Copertina.setImageBitmap(bitmap);
                        //supportStartPostponedEnterTransition();
                        ProgressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //supportStartPostponedEnterTransition();
                    }
                });
            } catch (IOException e ) {
                Copertina.setImageResource(R.drawable.ic_image_black_24dp);
            }
        }   else{
            Bitmap bitmap = BitmapFactory.decodeFile(libro.getPath());
            bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(140 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),Math.round(190 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),true);
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
                final ImageView sfondoSfocato = findViewById(R.id.sfocato);
                Bitmap map=takeScreenShot(BookActivity.this);
                Bitmap fast=fastblur(map);
                Drawable draw=new BitmapDrawable(getResources(),fast);
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View inflatedLayoutView = layoutInflater != null ? layoutInflater.inflate(R.layout.activity_barcode, null) : null;
                if (inflatedLayoutView != null) {
                    inflatedLayoutView.setAnimation(AnimationUtils.loadAnimation(BookActivity.this, R.anim.popup_enter));
                    final PopupWindow popupWindow = new PopupWindow(inflatedLayoutView);
                    sfondoSfocato.setImageDrawable(draw);
                    sfondoSfocato.setVisibility(View.VISIBLE);
                    popupWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setOutsideTouchable(false);
                    popupWindow.setFocusable(true);
                    popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0 , 0);
                    TextView descrizioneAcquisto = inflatedLayoutView.findViewById(R.id.messaggio);
                    Button acquista = inflatedLayoutView.findViewById(R.id.acquista_button);
                    ImageButton annulla = inflatedLayoutView.findViewById(R.id.annulla);
                    descrizioneAcquisto.setText("Se premi ACQUISTA accetti di acquistare questo libro e di procedere quindi alla procedura di pagamento");
                    acquista.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(),"Avviata procedura di acquisto" , Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        }
                    });
                    annulla.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sfondoSfocato.setVisibility(View.GONE);
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            sfondoSfocato.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void setupInfo(HashMap<String,String> transitionName) {
        TextView titolo = findViewById(R.id.titololibro);
        TextView autore = findViewById(R.id.autoreLibro);
        //Titolo.setTransitionName(transitionName.get(Titolo.getResources().getResourceName(Titolo.getId())));
        //Autore.setTransitionName(transitionName.get(Autore.getResources().getResourceName(Autore.getId())));
        TextView dataPubblicazione = findViewById(R.id.dataLibro);
        TextView prezzo = findViewById(R.id.prezzo);
        titolo.setText(libro.getTitolo());
        autore.setText(libro.getAutore());
        dataPubblicazione.setText("Anno:" + DateFormat.format("yyyy",libro.getPubblicazione()));
        prezzo.setText(String.valueOf(libro.getPrezzo())+ "\u20ac");
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
        mRecyclerView.setVisibility(View.GONE);
    }

    private void setupProgressBar() {
        ProgressBar = findViewById(R.id.progressBar);
        CaricamentoCommenti = findViewById(R.id.caricamentoCommenti);
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
                BookActivity.this.finishAfterTransition();
            }
        });
    }

    private void setupWindowAnimations() {
        getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.activity_fade));
        //getWindow().setReenterTransition(TransitionInflater.from(this).inflateTransition(R.transition.autoreout));
        //getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.book_uscita));
    }

}