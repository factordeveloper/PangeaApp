package com.masin.pangea.data.config

/*
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 * REFERENCIA: voces TTS disponibles
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *
 * No existe una lista única “en código” válida para todos los móviles: las voces las define el
 * motor instalado (p. ej. “Síntesis de voz de Google”, Samsung TTS, Pico, etc.) y los datos
 * de idioma descargados en Ajustes → Accesibilidad / Idiomas. Esta sección documenta locales y
 * ejemplos de nombres para que configures [TtsVoiceSelection] sin adivinar.
 *
 * ─── Cómo ver la lista REAL en tu dispositivo (Logcat) ───────────────────────────────────────
 * Tras tener un [TextToSpeech] inicializado con éxito:
 *
 *   import android.util.Log
 *   tts.voices?.sortedBy { it.name }?.forEach { v ->
 *       Log.d(
 *           "PangeaTTS",
 *           "${v.name} | locale=${v.locale} | features=${v.features} | " +
 *               "network=${v.isNetworkConnectionRequired} | quality=${v.quality} | latency=${v.latency}"
 *       )
 *   }
 *
 * Filtra en Logcat por etiqueta "PangeaTTS". Copia el `name` que quieras y úsalo como fragmento
 * en [TtsVoiceSelection.preferredVoiceNameContains].
 *
 * Si el nombre devuelve sufijos como `#female_1`, `#female_2`, `#male_1`, etc., el género va
 * explícito en la cadena (eso es lo más fiable tras escuchar la voz en el dispositivo).
 *
 * ─── Etiquetas de idioma (locale BCP 47) — úsalas en [TtsVoiceSelection.locale] como ─────────
 * El locale solo elige idioma/país; el género concreto lo fija el [Voice.name] que elijas abajo
 * (o el que quede por defecto al no usar [preferredVoiceNameContains]).
 *
 *   Locale.forLanguageTag("es-419")   // Español (Latinoamérica, neutro CLDR)
 *   Locale("es", "ES")                // España (equivalente a es-ES)
 *   Locale.forLanguageTag("es-ES")
 *   Locale.forLanguageTag("es-MX")    // México
 *   Locale.forLanguageTag("es-AR")    // Argentina
 *   Locale.forLanguageTag("es-CO")    // Colombia
 *   Locale.forLanguageTag("es-CL")    // Chile
 *   Locale.forLanguageTag("es-PE")    // Perú
 *   Locale.forLanguageTag("es-VE")    // Venezuela
 *   Locale.forLanguageTag("es-EC")    // Ecuador
 *   Locale.forLanguageTag("es-GT")    // Guatemala
 *   Locale.forLanguageTag("es-CU")    // Cuba
 *   Locale.forLanguageTag("es-BO")    // Bolivia
 *   Locale.forLanguageTag("es-DO")    // República Dominicana
 *   Locale.forLanguageTag("es-HN")    // Honduras
 *   Locale.forLanguageTag("es-PY")    // Paraguay
 *   Locale.forLanguageTag("es-SV")    // El Salvador
 *   Locale.forLanguageTag("es-NI")    // Nicaragua
 *   Locale.forLanguageTag("es-CR")    // Costa Rica
 *   Locale.forLanguageTag("es-PA")    // Panamá
 *   Locale.forLanguageTag("es-UY")    // Uruguay
 *   Locale.forLanguageTag("es-PR")    // Puerto Rico
 *   Locale.forLanguageTag("es-US")    // Estados Unidos
 *   Locale.forLanguageTag("es-GQ")    // Guinea Ecuatorial
 *
 * (Otros idiomas instalados en el sistema también aparecerán en tts.voices; esta app usa español.)
 *
 * ─── Ejemplos de [Voice.name] — motor Google en Android (con género) ─────────────────────────
 * Son identificadores internos; pueden cambiar entre versiones del APK “Speech Services by Google”
 * o no existir si usas otro motor. Sirven como guía para [preferredVoiceNameContains].
 *
 * Género:
 *   • “mujer / hombre” cuando en el índice público del motor (p. ej.
 *     https://dl.google.com/dl/android/tts/v2/voices-list-r2.proto ) aparece la etiqueta literal
 *     `female` / `male` junto al código → máxima confianza (hoy solo consta así para ana y sfb en español).
 *   • El resto: emparejamiento habitual en dispositivos recientes (par neuronal hembra/hombre)
 *     o criterio de foros / pruebas de audio; **confirma en tu terminal** si el acento te importa.
 *
 *   España (es-ES):
 *     es-es-x-ana-local, es-es-x-ana-network
 *         — mujer (figura como `female` en la lista proto pública de Google TTS v2).
 *     es-es-x-eea-local, es-es-x-eea-network
 *         — mujer (par neuronal habitual frente a eec; verificar en tu dispositivo).
 *     es-es-x-eec-local, es-es-x-eec-network
 *         — hombre (par neuronal habitual frente a eea; verificar en tu dispositivo).
 *
 *   México (es-MX):
 *     es-mx-x-msm-local, es-mx-x-msm-network
 *         — mujer (patrón habitual del motor Google en foros / dispositivos; verificar).
 *     es-mx-x-mxc-local, es-mx-x-mxc-network
 *         — hombre (patrón habitual del motor Google en foros / dispositivos; verificar).
 *
 *   Estados Unidos (es-US):
 *     es-us-x-sfb-local, es-us-x-sfb-network
 *         — mujer (figura como `female` en la lista proto pública de Google TTS v2).
 *     es-us-x-esd-local, es-us-x-esd-network
 *         — hombre (suele emparejarse con sfb en el motor Google; verificar).
 *
 *   Argentina (es-AR):
 *     es-ar-x-arc-local, es-ar-x-arc-network
 *         — mujer (voz principal habitual en listados del motor Google; sin proto público; verificar).
 *
 *   Colombia (es-CO):
 *     es-co-x-cog-local, es-co-x-cog-network
 *         — mujer (voz principal habitual en listados del motor Google; sin proto público; verificar).
 *
 *   Patrones genéricos en muchos locales:
 *     <idioma>-<país>-x-<código>-local     — voz almacenada en el dispositivo
 *     <idioma>-<país>-x-<código>-network   — puede requerir red
 *     …#female_N-local / …#male_N-local     — género explícito en el nombre (muy fiable)
 *
 * ─── Constantes de calidad (android.speech.tts.Voice) — referencia API ────────────────────────
 *   QUALITY_VERY_HIGH, QUALITY_HIGH, QUALITY_NORMAL, QUALITY_LOW, QUALITY_VERY_LOW
 *
 *   LATENCY_NORMAL, LATENCY_LOW, LATENCY_VERY_LOW  (valores en [Voice.latency])
 *
 * La selección en [PangeaTtsVoices.apply] prioriza voces que no exigen red y mayor [Voice.quality].
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 */

import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

/**
 * Selección de voz para el motor [TextToSpeech] del sistema.
 *
 * Cambia [PangeaTtsVoices.liaChat] y [PangeaTtsVoices.walkthrough] o usa [TtsVoicePresets].
 *
 * @param locale Idioma (y opcionalmente país) del TTS, por ejemplo [Locale] para español de España
 *   o [Locale.forLanguageTag] con `"es-419"` para español latinoamericano.
 * @param preferredVoiceNameContains Fragmento del nombre interno de la voz (depende del dispositivo;
 *   en Ajustes → Accesibilidad → salida de texto a voz puedes ver nombres). Si es null se elige una voz
 *   instalada del mismo idioma, priorizando la que no requiere red.
 * @param restrictToCountry Si no es null, solo se consideran voces cuyo [Voice.locale] use ese país ISO
 *   (por ejemplo `"ES"` o `"MX"`). Útil para fijar acento cuando hay varias en el mismo idioma.
 */
data class TtsVoiceSelection(
    val locale: Locale,
    val preferredVoiceNameContains: String? = null,
    val restrictToCountry: String? = null,
)

/**
 * Presets listos para usar. Fijan [restrictToCountry] para que el acento cambie al cambiar de país.
 *
 * Usa [esCountry] para cualquier código ISO (p. ej. `"PE"`, `"CL"`) sin nombre interno de voz.
 * Los fragmentos de nombre siguen el comentario de referencia al inicio de este archivo.
 */
object TtsVoicePresets {

    private fun es(
        countryIso: String,
        voiceNameContains: String? = null,
    ): TtsVoiceSelection = TtsVoiceSelection(
        locale = Locale("es", countryIso),
        preferredVoiceNameContains = voiceNameContains,
        restrictToCountry = countryIso,
    )

    /** Mejor voz offline/calidad de ese país, sin filtrar por [Voice.name]. */
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
 * **Cambia solo [liaChat] y [walkthrough]** (presets arriba o un [TtsVoiceSelection] a medida).
 */
object PangeaTtsVoices {

    val liaChat: TtsVoiceSelection = TtsVoicePresets.MEXICO_FEMALE
    val walkthrough: TtsVoiceSelection = TtsVoicePresets.COLOMBIA_FEMALE_TYPICAL

    /**
     * Aplica idioma y, en API 21+, la voz concreta del sistema.
     */
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
