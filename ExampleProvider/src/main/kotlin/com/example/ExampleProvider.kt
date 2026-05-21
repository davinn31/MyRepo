package com.example

import android.content.Context
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import org.jsoup.Jsoup

class ExampleProvider : MainAPI() {
    override var mainUrl = "https://movieboxonline.org"
    override var name = "MovieBox Pribadi"
    override val supportedTypes = setOf(TvType.Movie)
    override var lang = "en"
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val htmlMentah = app.get("$mainUrl/homepage").text
        val dokumen = Jsoup.parse(htmlMentah)
        val daftarFilm = ArrayList<SearchResponse>()

        dokumen.select("p.movie-card-title").forEach { elemen ->
            val judulFilm = elemen.text().trim()
            daftarFilm.add(newMovieSearchResponse(judulFilm, "$mainUrl/homepage", TvType.Movie) {
                this.posterUrl = "https://movieboxonline.org"
            })
        }

        return newHomePageResponse(listOf(HomePageList("Trending MovieBox", daftarFilm)), false)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
    }
}

@CloudstreamPlugin
class ExampleProviderPlugin : Plugin() {
    override fun load(context: Context) {
        plugin.registerProvider(ExampleProvider())
    }
}
