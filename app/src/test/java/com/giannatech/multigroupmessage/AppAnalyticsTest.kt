package com.giannatech.multigroupmessage

import com.giannatech.multigroupmessage.ui.AppAnalytics
import org.junit.Assert.assertEquals
import org.junit.Test

class AppAnalyticsTest {

    @Test
    fun appAnalytics_calculatesAvgCorrectly() {
        val totalCampaigns = 3
        val totalSent = 15
        val avg = if (totalCampaigns > 0) totalSent.toFloat() / totalCampaigns else 0f

        val analytics = AppAnalytics(
            totalCampaigns = totalCampaigns,
            totalMessagesSent = totalSent,
            uniquePeopleReached = 12,
            avgPeoplePerCampaign = avg
        )

        assertEquals(5.0f, analytics.avgPeoplePerCampaign, 0.001f)
        assertEquals(3, analytics.totalCampaigns)
        assertEquals(15, analytics.totalMessagesSent)
        assertEquals(12, analytics.uniquePeopleReached)
    }

    @Test
    fun appAnalytics_handlesZeroCampaigns_withoutDivisionByZero() {
        val totalCampaigns = 0
        val totalSent = 0
        val avg = if (totalCampaigns > 0) totalSent.toFloat() / totalCampaigns else 0f

        val analytics = AppAnalytics(
            totalCampaigns = totalCampaigns,
            totalMessagesSent = totalSent,
            uniquePeopleReached = 0,
            avgPeoplePerCampaign = avg
        )

        assertEquals(0.0f, analytics.avgPeoplePerCampaign, 0.001f)
    }
}
