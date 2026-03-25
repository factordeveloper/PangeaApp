package com.masin.pangea.data.config

/**
 * Voces del motor [android.speech.tts.TextToSpeech] del sistema.
 *
 * **Para cambiar voces:** edita solo [PangeaTtsAppVoices]. Los presets comunes están en
 * [TtsVoicePresets]; para algo distinto usa [TtsVoiceSelection] (locale, fragmento del
 * nombre interno de la voz, país opcional). Los nombres reales dependen del dispositivo;
 * con un [TextToSpeech] ya inicializado puedes listarlas en Logcat con
 * `tts.voices?.sortedBy { it.name }?.forEach { Log.d("PangeaTTS", "${it.name} | ${it.locale}") }`.
 * Patrones habituales Google: `es-XX-x-…-local` / `-network`; a veces incluyen `#female_N` en el nombre.
 */

import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

/**
 * @param locale Idioma (y opcionalmente país), p. ej. [Locale] `"es"`+`"ES"` o [Locale.forLanguageTag] `"es-419"`.
 * @param preferredVoiceNameContains Fragmento de [Voice.name] del sistema; si es null se elige entre voces del idioma (prioriza offline y calidad).
 * @param restrictToCountry Si no es null, solo voces con ese país ISO (p. ej. `"MX"`).
 */
data class TtsVoiceSelection(
    val locale: Locale,
    val preferredVoiceNameContains: String? = null,
    val restrictToCountry: String? = null,
)

/** Presets en español; combinan país + filtro opcional por nombre de voz. */
object TtsVoicePresets {

    private fun es(
        countryIso: String,
        voiceNameContains: String? = null,
    ): TtsVoiceSelection = TtsVoiceSelection(
        locale = Locale("es", countryIso),
        preferredVoiceNameContains = voiceNameContains,
        restrictToCountry = countryIso,
    )

    fun esCountry(countryIso: String): TtsVoiceSelection = es(countryIso, null)

    val SPAIN_FEMALE_CLASSIC = es("ES", "es-es-x-ana")
    val SPAIN_FEMALE_NEURAL = es("ES", "es-es-x-eea")
    val SPAIN_MALE_NEURAL = es("ES", "es-es-x-eec")
    val SPAIN_ANY = es("ES", null)

    val MEXICO_FEMALE = es("MX", "es-mx-x-msm")
    val MEXICO_MALE = es("MX", "es-mx-x-mxc")
    val MEXICO_ANY = es("MX", null)

    val US_FEMALE = es("US", "es-us-x-sfb")
    val US_MALE = es("US", "es-us-x-esd")
    val US_ANY = es("US", null)

    val ARGENTINA_ANY = es("AR", null)
    val ARGENTINA_FEMALE_TYPICAL = es("AR", "es-ar-x-arc")
    val COLOMBIA_ANY = es("CO", null)
    val COLOMBIA_FEMALE_TYPICAL = es("CO", "es-co-x-cog")

    val LATAM_ES_419: TtsVoiceSelection = TtsVoiceSelection(
        locale = Locale.forLanguageTag("es-419"),
        preferredVoiceNameContains = null,
        restrictToCountry = null,
    )
}

/**
 * **Único sitio para asignar las voces de la app** (chat LIA y recorrido inicial).
 *
 * Ejemplos rápidos:
 * - `liaChat = TtsVoicePresets.MEXICO_FEMALE`
 * - `walkthrough = TtsVoiceSelection(Locale("es", "ES"), "es-es-x-ana", "ES")`
 */
object PangeaTtsAppVoices {

    /** LIA — [com.masin.pangea.presentation.ui.screens.LiaVoiceCallScreen] */
    val liaChat: TtsVoiceSelection = TtsVoicePresets.MEXICO_FEMALE

    /** Recorrido / onboarding inicial — [com.masin.pangea.presentation.ui.screens.WalkthroughScreen] */
    val walkthrough: TtsVoiceSelection = TtsVoicePresets.COLOMBIA_FEMALE_TYPICAL
}

/** Aplica idioma y, en API 21+, la voz elegida. Usa [liaChat] / [walkthrough] (delegan en [PangeaTtsAppVoices]). */
object PangeaTtsVoices {

    val liaChat: TtsVoiceSelection get() = PangeaTtsAppVoices.liaChat
    val walkthrough: TtsVoiceSelection get() = PangeaTtsAppVoices.walkthrough

    fun apply(tts: TextToSpeech, selection: TtsVoiceSelection) {
        var langResult = tts.setLanguage(selection.locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(Locale("es", "ES"))
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return

        val voices = tts.voices ?: return
        val candidates = voices.filter { matchesSelection(it, selection) }
        if (candidates.isEmpty()) return

        val named = selection.preferredVoiceNameContains?.takeIf { it.isNotBlank() }?.let { needle ->
            candidates.filter { it.name.contains(needle, ignoreCase = true) }
        }
        val pool = named?.takeIf { it.isNotEmpty() } ?: candidates

        val voice = pool.minWithOrNull(
            compareBy<Voice> { if (it.isNetworkConnectionRequired) 1 else 0 }
                .thenBy { -it.quality }
        ) ?: return
        runCatching { tts.voice = voice }
    }

    private fun matchesSelection(voice: Voice, selection: TtsVoiceSelection): Boolean {
        val vl = voice.locale
        if (!vl.language.equals(selection.locale.language, ignoreCase = true)) return false
        selection.restrictToCountry?.takeIf { it.isNotBlank() }?.let { country ->
            if (!vl.country.equals(country, ignoreCase = true)) return false
        }
        return true
    }
}
