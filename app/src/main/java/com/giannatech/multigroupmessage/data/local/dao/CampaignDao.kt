package com.giannatech.multigroupmessage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.giannatech.multigroupmessage.data.local.entity.CampaignEntity
import com.giannatech.multigroupmessage.data.local.entity.DispatchLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY timestamp DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :campaignId")
    suspend fun getCampaignById(campaignId: Long): CampaignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignEntity): Long

    @Query("UPDATE campaigns SET sentCount = :sentCount WHERE id = :campaignId")
    suspend fun updateSentCount(campaignId: Long, sentCount: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispatchLog(log: DispatchLogEntity): Long

    @Query("SELECT * FROM dispatch_logs WHERE campaignId = :campaignId ORDER BY timestamp ASC")
    fun getDispatchLogsForCampaign(campaignId: Long): Flow<List<DispatchLogEntity>>

    @Query("SELECT COUNT(*) FROM campaigns")
    fun getTotalCampaignsCount(): Flow<Int>

    @Query("SELECT SUM(sentCount) FROM campaigns")
    fun getTotalMessagesSentCount(): Flow<Int?>

    @Query("SELECT COUNT(DISTINCT phoneNumber) FROM dispatch_logs WHERE status = 'SENT'")
    fun getTotalUniquePeopleReachedCount(): Flow<Int>
}
