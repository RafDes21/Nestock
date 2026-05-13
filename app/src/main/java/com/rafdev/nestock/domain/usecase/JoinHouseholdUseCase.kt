package com.rafdev.nestock.domain.usecase

import com.rafdev.nestock.data.repository.HouseholdRepository
import javax.inject.Inject

class JoinHouseholdUseCase @Inject constructor(
    private val householdRepository: HouseholdRepository
) {
    suspend operator fun invoke(inviteCode: String): Result<String> =
        householdRepository.joinHousehold(inviteCode.trim().uppercase())
}
