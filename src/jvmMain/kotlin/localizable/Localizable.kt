package localizable

import localizable.strings.Strings
import localizable.strings.StringsEN
import localizable.strings.StringsZH
import java.util.Locale

object Localizable {

    var locale = Locale.getDefault()

    fun strings(): Strings {
        return when (locale.language) {
            Locale.ENGLISH.language -> StringsEN
            else -> StringsZH
        }
    }
}