package de.md5lukas.waypoints.lang

class BooleanTranslationOption(
    private val translationLoader: TranslationLoader,
    private val key: String,
    private val default: Boolean,
) : AbstractTranslation {

  init {
    translationLoader.registerTranslationWrapper(this)
  }

  fun value(): Boolean = translationLoader[key].toBooleanStrictOrNull() ?: default

  override fun reset() {}

  override fun getKeys(): Array<String> = arrayOf(key)
}
