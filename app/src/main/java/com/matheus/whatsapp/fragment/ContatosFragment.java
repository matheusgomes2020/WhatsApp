package com.matheus.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.matheus.whatsapp.R;
import com.matheus.whatsapp.activity.ChatActivity;
import com.matheus.whatsapp.adapter.ContatosAdapter;
import com.matheus.whatsapp.config.ConfiguracaoFirebase;
import com.matheus.whatsapp.helper.RecyclerItemClickListener;
import com.matheus.whatsapp.helper.UsuarioFirebase;
import com.matheus.whatsapp.model.Usuario;

import java.util.ArrayList;


public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listacontatos = new ArrayList<>();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;



    public ContatosFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_contatos, container, false);

       //Configurações iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        //Configurar adapter
        adapter = new ContatosAdapter( listacontatos, getActivity() );

        //Configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewListaContatos.setLayoutManager( layoutManager );
        recyclerViewListaContatos.setHasFixedSize( true );
        recyclerViewListaContatos.setAdapter( adapter );

        //Configurar evento de clique no recyclerview
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Intent i = new Intent( getActivity(), ChatActivity.class);
                                startActivity( i );

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );


       return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener( valueEventListenerContatos );
    }

    public void  recuperarContatos(){

        valueEventListenerContatos = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listacontatos.clear();

                for ( DataSnapshot dados : snapshot.getChildren() ){

                    Usuario usuario = dados.getValue( Usuario.class );

                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if ( !emailUsuarioAtual.equals( usuario.getEmail() ) ) {
                        listacontatos.add( usuario );
                    }

                    listacontatos.add( usuario );
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}