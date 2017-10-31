package com.example.carlos.biketrip;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.DisplayContext;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements Main1Fragment.OnFragmentInteractionListener, Main2Fragment.OnFragmentInteractionListener,
        Main3Fragment.OnFragmentInteractionListener{


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    String TAG="TAG" ;

    Button btnIniciarSesion;
    Button btnCrearCuenta;
    EditText mUser;
    EditText mPassword;


    //firebase	authentication

    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        mAuth =	FirebaseAuth.getInstance();
        mAuthListener =	new	FirebaseAuth.AuthStateListener()	{
            @Override
            public	void	onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)	{
                FirebaseUser user	=	firebaseAuth.getCurrentUser();
                if	(user	!=	null)	{
                    //	User	is	signed	in
                    Log.d(TAG,	"onAuthStateChanged:signed_in:"	+	user.getUid());


                    Intent intent= new Intent(getBaseContext(), MenuPrincipal.class);
                    startActivity(intent);

                }	else	{
                    //	User	is	signed	out
                    Log.d(TAG,	"onAuthStateChanged:signed_out");
                }
            }
        };


        btnIniciarSesion = (Button)findViewById(R.id.btnIniciarSesion);
        btnCrearCuenta = (Button)findViewById(R.id.btnCrearCuenta);
        mUser = (EditText)findViewById(R.id.etEmail);
        mPassword = (EditText)findViewById(R.id.etPass);

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_DONE){
                    signInUser();
                    return true;
                }
                return false;
            }
        });

        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(), CrearCuenta.class);
                startActivity(intent);
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });


    }


    protected void signInUser(){
        if(validateForm()){

            String email = mUser.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());

                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidUserException e) {
                                    mUser.setError(getString(R.string.errorUsuarioInvalido));
                                    mUser.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    mUser.setError(getString(R.string.errorUsuarioContraseñaInvalida));
                                    mUser.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                mUser.setText("");
                                mPassword.setText("");
                            }
                            else{

                                FirebaseUser user	=	mAuth.getCurrentUser();
                                if(user!=null){	//Update	user	Info
                                    Intent intent= new Intent(getBaseContext(), MenuPrincipal.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
        }
    }



    private	boolean validateForm()	{
        boolean valid	=	true;
        String	email	=	mUser.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            mUser.setError("Required.");
            valid	=	false;
        }	else	{
            mUser.setError(null);
        }
        String	password	=	mPassword.getText().toString();
        if	(TextUtils.isEmpty(password))	 {
            mPassword.setError("Required.");
            valid	=	false;
        }	else	{
            mPassword.setError(null);
        }
        return	valid;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {

            Fragment fragment = null;

            switch (sectionNumber){

                case 1:
                    fragment = new Main1Fragment();
                    break;
                case 2:
                    fragment = new Main2Fragment();
                    break;
                case 3:
                    fragment = new Main3Fragment();
                    break;
            }
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);




            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }


    @Override
    protected	void	onStart()	 {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public	void	onStop()	 {
        super.onStop();
        if	(mAuthListener !=	null)	{
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
