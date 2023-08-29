package br.com.igorbag.githubsearch.ui

import RepositoryAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.databinding.ActivityMainBinding
import br.com.igorbag.githubsearch.domain.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gitHubService: GitHubService
    private lateinit var repositoryAdapter: RepositoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRetrofit()
        setupListeners()
        showUserName()


        repositoryAdapter = RepositoryAdapter(emptyList()) // Inicialize com uma lista vazia, pois você pode definir os dados mais tarde
        binding.rvListaRepositories.adapter = repositoryAdapter
    }

    override fun onStart() {
        super.onStart()
        getAllReposByUserName()
    }
    // Método responsável por configurar os listeners click da tela
    private fun setupListeners() {
        binding.btnConfirmar.setOnClickListener { getAllReposByUserName() }
    }

    // Salvar o usuário preenchido no EditText utilizando SharedPreferences
    private fun saveUserLocal(user: EditText) {
        val username = user.text.toString()
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(getString(R.string.save), username)
            apply()
        }
    }

    // Exibir o nome do usuário salvo, se existir, no EditText
    private fun showUserName() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val savedUsername = sharedPref.getString(getString(R.string.save), "")
        binding.etNomeUsuario.setText(savedUsername)
    }

    // Método responsável por fazer a configuração base do Retrofit

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/") // URL base correta da API do GitHub
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        gitHubService = retrofit.create(GitHubService::class.java)
    }


    private fun getAllReposByUserName() {
        val username = binding.etNomeUsuario.text.toString()
        if (username.isNotEmpty()) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val repos = gitHubService.getAllRepositoriesByUser(username)
                        runOnUiThread {
                            setupAdapter(repos)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Método responsável por realizar a configuração do adapter
    private fun setupAdapter(list: List<Repository>) {
        // Inicialize o adaptador com a lista de repositórios
        repositoryAdapter = RepositoryAdapter(list)

        // Configurar o clique em um item do repositório
        repositoryAdapter.carItemClickListener = { repository ->
            // Lide com o clique em um item do repositório aqui
        }

        // Configurar o clique no botão de compartilhamento
        repositoryAdapter.btnShareClickListener = { repository ->
            // Lide com o clique no botão de compartilhamento aqui
        }

        // Configure o RecyclerView com o adaptador
        binding.rvListaRepositories.adapter = repositoryAdapter
    }


    // Método responsável por compartilhar o link do repositório selecionado
    private fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Método responsável por abrir o navegador com o link do repositório
    private fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }
}
