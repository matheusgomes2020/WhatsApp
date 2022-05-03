package com.matheus.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.matheus.whatsapp.R;
import com.matheus.whatsapp.adapter.ContatosAdapter;
import com.matheus.whatsapp.adapter.GrupoSelecionadoAdapter;
import com.matheus.whatsapp.config.ConfiguracaoFirebase;
import com.matheus.whatsapp.helper.RecyclerItemClickListener;
import com.matheus.whatsapp.helper.UsuarioFirebase;
import com.matheus.whatsapp.model.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerMembrosSelecionados, recyclerMembros;
    private ContatosAdapter contatosAdapter;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private List<Usuario> listaMembros = new ArrayList<>();
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerMembros;
    private FirebaseUser usuarioAtual;
    private FloatingActionButton fabAvancarCadastro;

    private AppBarConfiguration appBarConfiguration;

    public void atualizarMembrosToolbar(){

        int totalSelecionados = listaMembrosSelecionados.size();
        int total = listaMembros.size() + totalSelecionados;
        getSupportActionBar().setSubtitle( totalSelecionados + " de " + total + " selecionados" );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setTitle("Novo grupo");

        //Configurações iniciais
        recyclerMembros = findViewById( R.id.recyclerMembros );
        recyclerMembrosSelecionados = findViewById( R.id.recyclerMembrosSelecionados );
        fabAvancarCadastro = findViewById( R.id.fabAvancarCadastro);

        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child( "usuarios" );
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        //Configurar adapter
        contatosAdapter = new ContatosAdapter( listaMembros, getApplicationContext() );

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerMembros.setLayoutManager( layoutManager );
        recyclerMembros.setHasFixedSize( true );
        recyclerMembros.setAdapter( contatosAdapter );

        recyclerMembros.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembros,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Usuario usuarioSelecionado = listaMembros.get( position );

                                //Revomer usuario selecionado da lista
                                listaMembros.remove( usuarioSelecionado );
                                contatosAdapter.notifyDataSetChanged();

                                //Adiciona usuario na nova lista de selecionados
                                listaMembrosSelecionados.add( usuarioSelecionado );
                                grupoSelecionadoAdapter.notifyDataSetChanged();

                                atualizarMembrosToolbar();


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));


        //configurar floating action button
        fabAvancarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( getApplicationContext(), CadastroGrupoActivity.class );
                i.putExtra(  "membros", (Serializable) listaMembrosSelecionados );
                startActivity( i );

            }
        });

        //Configurar recyclerview para os membros selecionados
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter( listaMembrosSelecionados, getApplicationContext() );

        RecyclerView.LayoutManager layoutManagerHorinzontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerMembrosSelecionados.setLayoutManager( layoutManagerHorinzontal );
        recyclerMembrosSelecionados.setHasFixedSize( true );
        recyclerMembrosSelecionados.setAdapter( grupoSelecionadoAdapter );

        recyclerMembrosSelecionados.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembrosSelecionados,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Usuario usuarioSelecionado = listaMembrosSelecionados.get( position );

                                //Remover da listagem de membros selecionados
                                listaMembrosSelecionados.remove( usuarioSelecionado );
                                grupoSelecionadoAdapter.notifyDataSetChanged();

                                //Adicionar a listagem de membros
                                listaMembros.add( usuarioSelecionado );
                                contatosAdapter.notifyDataSetChanged();

                                atualizarMembrosToolbar();

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

    }

    public void recuperarContatos(){

        valueEventListenerMembros = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot dados: dataSnapshot.getChildren() ){

                    Usuario usuario = dados.getValue( Usuario.class );

                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if ( !emailUsuarioAtual.equals( usuario.getEmail() ) ){
                        listaMembros.add( usuario );
                    }


                }

                contatosAdapter.notifyDataSetChanged();
                atualizarMembrosToolbar();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerMembros);
    }

}

