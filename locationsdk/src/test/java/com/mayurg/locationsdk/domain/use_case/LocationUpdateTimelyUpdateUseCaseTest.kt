package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.model.LocationUpdateResult
import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import com.mayurg.locationsdk.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyDouble
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class LocationUpdateTimelyUpdateUseCaseTest {

    @Mock
    private lateinit var locationClient: LocationClient

    @Mock
    private lateinit var locationApiRepository: LocationApiRepository

    private lateinit var locationUpdateTimelyUpdateUseCase: LocationUpdateTimelyUpdateUseCase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        locationUpdateTimelyUpdateUseCase = LocationUpdateTimelyUpdateUseCase(locationClient, locationApiRepository, CoroutineScope(testDispatcher))
    }

    @Test
    fun `updateTimelyLocation returns success result`() = runTest {
        val location = Pair(1.0, 1.0)
        val interval = 1000L
        `when`(locationClient.getLocationUpdates(interval)).thenReturn(flowOf(location))
        `when`(locationApiRepository.updateLocation(location)).thenReturn(Result.Success(
            LocationUpdateResult("Success")
        ))

        val onLocationUpdated: OnLocationUpdated = mock(OnLocationUpdated::class.java)
        val onError: OnError = mock(OnError::class.java)

        locationUpdateTimelyUpdateUseCase.updateTimelyLocation(interval,  onLocationUpdated::invoke, onError::invoke)

        verify(onLocationUpdated).invoke(location.first, location.second)
        verify(onError, never()).invoke(anyInt(), anyString())
    }

    @Test
    fun `updateTimelyLocation returns error result`() = runTest {
        val location = Pair(1.0, 1.0)
        val interval = 1000L
        `when`(locationClient.getLocationUpdates(interval)).thenReturn(flowOf(location))
        `when`(locationApiRepository.updateLocation(location)).thenReturn(Result.Failure(500, "Error"))

        val onLocationUpdated: OnLocationUpdated = mock(OnLocationUpdated::class.java)
        val onError: OnError = mock(OnError::class.java)

        locationUpdateTimelyUpdateUseCase.updateTimelyLocation(interval, onLocationUpdated::invoke, onError::invoke)

        verify(onError).invoke(500, "Error")
        verify(onLocationUpdated, never()).invoke(anyDouble(), anyDouble())
    }
}