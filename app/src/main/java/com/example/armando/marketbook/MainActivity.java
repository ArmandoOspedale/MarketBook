package com.example.armando.marketbook;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //TODO fragment Negozio,Download -> ok
    //TODO barra di ricerca, implementare il filtraggio -> ok
    //TODO sezione commenti utente > ok
    //TODO activity specifica autore > ok
    //TODO prelevare i dati dal database, mapping del database -> ok
    //TODO Sistemare ActionBar -> ok
    //TODO Analisi codice a barre libro per ottenere versione digitale a partire dalla versione fisica del libro -> ok
    //TODO altri ed eventuali...

    //View
    private Toolbar toolbar;
    private ImageView sfondoSfocato;

    //Database
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            negozioFragment negozioFragment= new negozioFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_layout, negozioFragment);
            fragmentTransaction.commit();
        }

        setupWindowAnimations();
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.m_search);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate).setBackground(null);
        EditText et= mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        et.setHintTextColor(getResources().getColor(R.color.sfondomedio));
        et.setHint("Cerca libri, autori...");
        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        final RecyclerView risultati = findViewById(R.id.risultati);
        sfondoSfocato = findViewById(R.id.sfocato);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        risultati.setLayoutManager(layoutManager);
        risultati.setHasFixedSize(true);
        risultati.setLayoutManager(layoutManager);
        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Bitmap map=takeScreenShot(MainActivity.this);
                Bitmap fast=fastblur(map);
                Drawable draw=new BitmapDrawable(getResources(),fast);
                sfondoSfocato.setImageDrawable(draw);
                risultati.setVisibility(View.VISIBLE);
                sfondoSfocato.setVisibility(View.VISIBLE);
                db.collection("Index").document("sZYBGqY7z7gXeFEjodCW").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Map<String,Object> x =documentSnapshot.getData();
                        List<String> indiceLibri = (List<String>) x.get("libri");
                        List<String> indiceAutori = (List<String>) x.get("autori");
                        ricerca(mSearchView,risultati,indiceLibri,indiceAutori);
                    }
                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                risultati.setVisibility(View.GONE);
                new RisultatiAdapter(getApplicationContext()).destroy();
                sfondoSfocato.setVisibility(View.GONE);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private List<String> buildArray(List<String> indice, String stringa) {
        List<String> riscontri = new ArrayList<>();
        if(!stringa.equals("")){
            for (int i=0;i<indice.size();i++){
                if(indice.get(i).toLowerCase().contains(stringa.toLowerCase())){
                    riscontri.add(indice.get(i));
                }
            }
        }
        return riscontri;
    }

    private void ricerca(SearchView mSearchView, final RecyclerView risultati, final List<String> indiceLibri, final List<String> indiceAutori) {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                final List<Object> risultatiQuery = new ArrayList<>();
                //if(!newText.equals("")){
                    List<String> riscontriLibri = buildArray(indiceLibri,newText);
                    final List<String> riscontriAutori = buildArray(indiceAutori,newText);
                    ArrayList<Task> taskArrayList = new ArrayList<>();
                    if(riscontriAutori.size()>5){
                        for (int i=0;i<5;i++){ //prendo i primi 5 risultati
                            taskArrayList.add(db.collection("Autori").whereEqualTo("Nome",riscontriAutori.get(i)).get()) ;
                        }
                    }else{
                        for (int i=0;i<riscontriAutori.size();i++){ //prendo i primi 5 risultati
                            taskArrayList.add(db.collection("Autori").whereEqualTo("Nome",riscontriAutori.get(i)).get()) ;
                        }
                    }
                    if(riscontriLibri.size()>5){
                        for (int i=0;i<5;i++){ //prendo i primi 5 risultati
                            taskArrayList.add(db.collection("Libri").whereEqualTo("Titolo",riscontriLibri.get(i)).get());
                        }
                    }else{
                        for (int i=0;i<riscontriLibri.size();i++){ //prendo i primi 5 risultati
                            taskArrayList.add(db.collection("Libri").whereEqualTo("Titolo",riscontriLibri.get(i)).get());
                        }
                    }
                    Tasks.whenAllSuccess(taskArrayList.toArray(new Task[taskArrayList.size()])).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> list) {
                            List<List<DocumentSnapshot>> Documenti= new ArrayList<>();
                            for(int i=0;i<list.size();i++){
                                QuerySnapshot QueryLibri = (QuerySnapshot) list.get(i);
                                Documenti.add(QueryLibri.getDocuments());
                            }
                            for(int y=0; y< Documenti.size(); y++){
                                Book book = Documenti.get(y).get(0).toObject(Book.class);
                                if(book.getTitolo()==null){
                                    Autore autore = Documenti.get(y).get(0).toObject(Autore.class);
                                    autore.setID(Documenti.get(y).get(0).getId());
                                    risultatiQuery.add(autore);
                                }
                                else {
                                    book.setRiferimento(Documenti.get(y).get(0).getReference().getPath());
                                    risultatiQuery.add(book);
                                }
                            }
                            RisultatiAdapter adapter = new RisultatiAdapter(getApplicationContext(),risultatiQuery);
                            risultati.setAdapter(adapter);
                            risultati.setFocusable(true);
                            adapter.SetOnItemClickListener(new RisultatiAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Object object,List<View> shared, HashMap<String,String> transitionName) {
                                    try{
                                        Book book = (Book) object;
                                        Intent intent = new Intent(MainActivity.this,BookActivity.class);
                                        setIntent(intent);
                                        intent.putExtra("LIBRO",book);
                                        intent.putExtra("TRANSITION_NAME",transitionName);
                                        View statusBar = findViewById(android.R.id.statusBarBackground);
                                        View navigationBar = findViewById(android.R.id.navigationBarBackground);
                                        List<Pair<View, String>> pairs = new ArrayList<>();
                                        if (statusBar != null) {
                                            pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                                        }
                                        if (navigationBar != null) {
                                            pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                                        }
                                        if (toolbar != null) {
                                            pairs.add(Pair.create((View)toolbar, toolbar.getTransitionName()));
                                        }for (int i=0;i<shared.size();i++){
                                            pairs.add(Pair.create(shared.get(i), shared.get(i).getTransitionName()));
                                        }
                                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs.toArray(new Pair[pairs.size()])).toBundle());
                                    }catch (Exception e){
                                        Autore autore = (Autore) object;
                                        Intent intent = new Intent(MainActivity.this,AutoreActivity.class);
                                        intent.putExtra("ID",autore.getID());
                                        View statusBar = findViewById(android.R.id.statusBarBackground);
                                        View navigationBar = findViewById(android.R.id.navigationBarBackground);
                                        List<Pair<View, String>> pairs = new ArrayList<>();
                                        if (statusBar != null) {
                                            pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                                        }
                                        if (navigationBar != null) {
                                            pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                                        }
                                        if (toolbar != null) {
                                            pairs.add(Pair.create((View)toolbar, toolbar.getTransitionName()));
                                        }
                                        Bundle options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs.toArray(new Pair[pairs.size()])).toBundle();
                                        startActivity(intent, options);
                                    }
                                }
                            });
                        }
                    });
                return false;
            }
        });
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            setupBottonNavigationView();
        } else {
            signInAnonymously();
        }

    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                setupBottonNavigationView();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Errore", "signInAnonymously:FAILURE", exception);
            }
        });
    }

    private void setupBottonNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_shop:
                        selectedFragment = negozioFragment.newInstance();
                        break;
                    case R.id.navigation_download:
                        selectedFragment = downloadFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
    }

    private void setupWindowAnimations() {
        getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.activity_slide));
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

    @Override
    public void onBackPressed() {
        if(sfondoSfocato!=null){
            sfondoSfocato.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //downloadFragment.onActivityResult(requestCode, resultCode, data);
    }
}