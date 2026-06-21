package com.life.habitbloom.data

import android.content.Context
import com.life.habitbloom.model.CompanionType
import com.life.habitbloom.model.FocusType
import com.life.habitbloom.model.GenderType
import com.life.habitbloom.model.UserProfile
import java.util.Calendar

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("habitbloom_prefs", Context.MODE_PRIVATE)

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putBoolean("onboardingCompleted", profile.onboardingCompleted)
            .putString("userName", profile.userName)
            .putString("gender", profile.gender.name)
            .putString("companionType", profile.companionType.name)
            .putString("companionName", profile.companionName)
            .putStringSet("focusTypes", profile.focusTypes.map { it.name }.toSet())
            .putString("wakeUpTime", profile.wakeUpTime)
            .putInt("sleepHours", profile.sleepHours)
            .apply()
    }

    fun loadProfile(): UserProfile {
        val focusNames = prefs.getStringSet("focusTypes", emptySet()) ?: emptySet()
        val focusTypes = focusNames.mapNotNull {
            runCatching { FocusType.valueOf(it) }.getOrNull()
        }

        return UserProfile(
            onboardingCompleted = prefs.getBoolean("onboardingCompleted", false),
            userName = prefs.getString("userName", "") ?: "",
            gender = runCatching {
                GenderType.valueOf(prefs.getString("gender", GenderType.GIRL.name)!!)
            }.getOrDefault(GenderType.GIRL),
            companionType = runCatching {
                CompanionType.valueOf(prefs.getString("companionType", CompanionType.SEED.name)!!)
            }.getOrDefault(CompanionType.SEED),
            companionName = prefs.getString("companionName", "") ?: "",
            focusTypes = if (focusTypes.isNotEmpty()) focusTypes else listOf(FocusType.MOVEMENT),
            wakeUpTime = prefs.getString("wakeUpTime", "07:00") ?: "07:00",
            sleepHours = prefs.getInt("sleepHours", 8)
        )
    }

    fun saveHomeState(
        totalXp: Int,
        streak: Int,
        completedDate: Int,
        completedChallenges: Set<Int>
    ) {
        prefs.edit()
            .putInt("totalXp", totalXp)
            .putInt("streak", streak)
            .putInt("completedDate", completedDate)
            .putStringSet("completedChallenges", completedChallenges.map { it.toString() }.toSet())
            .apply()
    }

    fun getTotalXp(): Int = prefs.getInt("totalXp", 0)

    fun getStreak(): Int = prefs.getInt("streak", 0)

    fun getCompletedDate(): Int = prefs.getInt("completedDate", -1)

    fun getCompletedChallenges(): Set<Int> {
        return prefs.getStringSet("completedChallenges", emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
    }

    fun saveHydrationState(
        waterCount: Int,
        lastDrinkTimeMillis: Long,
        hydrationDate: Int
    ) {
        prefs.edit()
            .putInt("hydrationWaterCount", waterCount)
            .putLong("hydrationLastDrinkTimeMillis", lastDrinkTimeMillis)
            .putInt("hydrationDate", hydrationDate)
            .apply()
    }

    fun getHydrationWaterCount(): Int {
        return prefs.getInt("hydrationWaterCount", 0)
    }

    fun getHydrationLastDrinkTimeMillis(): Long {
        return prefs.getLong("hydrationLastDrinkTimeMillis", 0L)
    }

    fun getHydrationDate(): Int {
        return prefs.getInt("hydrationDate", -1)
    }

    fun today(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }
}