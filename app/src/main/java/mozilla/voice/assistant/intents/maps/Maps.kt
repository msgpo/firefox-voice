package mozilla.voice.assistant.intents.maps

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.VisibleForTesting
import mozilla.voice.assistant.IntentRunner
import mozilla.voice.assistant.MatcherResult

class Maps {
    companion object {
        @VisibleForTesting
        internal const val LOCATION_KEY = "location"
        @VisibleForTesting
        internal const val THING_KEY = "thing"
        @VisibleForTesting
        internal const val MODE_KEY = "mode"
        @VisibleForTesting
        internal const val BIKE_MODE = "b"
        @VisibleForTesting
        internal const val WALK_MODE = "w"

        fun register() {
            IntentRunner.registerIntent(
                mozilla.voice.assistant.Intent(
                    "Maps - navigate",
                    "Navigate to a specific destination",
                    listOf(
                        "Show me how to get to Carnegie Hall",
                        "Navigate to 345 Spear Street",
                        "What's the best way to walk to the Bay Bridge?"
                    ),
                    listOf(
                        "(give me|i'd like) (car|driving|) directions to [$LOCATION_KEY]",
                        "navigate (by car|) to [$LOCATION_KEY]",
                        "(show|tell) (me|) how to (bike|bicycle|cycle) to [$LOCATION_KEY] [$MODE_KEY=$BIKE_MODE]",
                        "(show|tell) (me|) how to get to [$LOCATION_KEY] by (bike|biking|bicycle|bicycling|cycling) [$MODE_KEY=$BIKE_MODE]",
                        "(show|tell) (me|) how to walk to [$LOCATION_KEY] [$MODE_KEY=$WALK_MODE]",
                        "(show|tell) (me|) how to get to [$LOCATION_KEY] (on foot|by foot|by walking) [$MODE_KEY=$WALK_MODE]",
                        "(show|tell) me (how to get|how to drive|directions|the way) to [$LOCATION_KEY]",
                        "(what's|show me|tell me) the (best|fastest|quickest) way to get to [$LOCATION_KEY] (by car|)",
                        "(what's|show me|tell me) the (best|fastest|quickest) way to drive to [$LOCATION_KEY] (by car|)",
                        "(what's|show me|tell me) the (best|fastest|quickest) way to get to [$LOCATION_KEY] (on foot|by foot|by walking) [$MODE_KEY=$WALK_MODE]",
                        "(what's|show me|tell me) the (best|fastest|quickest) way to walk to [$LOCATION_KEY] [$MODE_KEY=$WALK_MODE]",
                        "(what's|show me|tell me) the (best|fastest|quickest) way to get to [$LOCATION_KEY] (by|on) (bike|biking|bicycle|bicycling|cycling) [$MODE_KEY=$BIKE_MODE]",
                        "(what's|show me|tell me) the (best|fastest|quickest) way to (bike|bicycle|cycle) to [$LOCATION_KEY] [$MODE_KEY=$BIKE_MODE]"
                    ),
                    ::createNavigateIntent
                )
            )

            IntentRunner.registerIntent(
                mozilla.voice.assistant.Intent(
                    "Maps - recommend",
                    "Find a certain type of place",
                    listOf("Where can I buy pizza?", "What's a good place to get sushi?", "Where's good ramen in Oakland?"),
                    listOf(
                        "where (can i|to) (get|find|buy|eat) [$THING_KEY] (in|near) [$LOCATION_KEY]",
                        "where (can i|to) (get|find|buy|eat) [$THING_KEY]",
                        "(where's|where is|what's|what is) (the|a) (near|nearest|close|closest|good|best|) (place for|place to get|) [$THING_KEY] (in|near) [$LOCATION_KEY]",
                        "(where's|where is|what's|what is) (the|a) (near|nearest|close|closest|good|best|) (place for|place to get|) [$THING_KEY]",
                        "(search for|look for|find) [$THING_KEY] (in|near) [$LOCATION_KEY]",
                        "(search for|look for|find) nearby [$THING_KEY]"
                    ),
                    ::createSearchIntent
                )
            )
        }

        private fun createNavigateIntent(
            mr: MatcherResult,
            @Suppress("UNUSED_PARAMETER") context: Context?
        ): android.content.Intent? =
            mr.slots[LOCATION_KEY]?.let {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=$it${mr.parameters[MODE_KEY].toModeSuffix()}")
                )
            }

        private fun createSearchIntent(
            mr: MatcherResult,
            @Suppress("UNUSED_PARAMETER") context: Context?
        ): android.content.Intent? =
            mr.slots[THING_KEY]?.let { thing ->
                val suffix = mr.slots[LOCATION_KEY]?.let { "in $it" } ?: ""
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0.0?q=$thing$suffix")
                )
            }
    }
}

fun String?.toModeSuffix() = if (this.isNullOrEmpty()) "" else "&mode=$this"
