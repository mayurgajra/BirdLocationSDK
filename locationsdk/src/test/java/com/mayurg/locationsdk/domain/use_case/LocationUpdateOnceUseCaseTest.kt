package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.model.LocationUpdateResult
import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import com.mayurg.locationsdk.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LocationUpdateOnceUseCaseTest {

    @Mock
    private lateinit var locationClient: LocationClient

    @Mock
    private lateinit var locationApiRepository: LocationApiRepository

    private lateinit var locationUpdateOnceUseCase: LocationUpdateOnceUseCase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        locationUpdateOnceUseCase = LocationUpdateOnceUseCase(locationClient, locationApiRepository, CoroutineScope(testDispatcher))
    }

    @Test
    fun `updateLocationOnce returns location on success`() = runTest {
        val location = Pair(1.0, 1.0)
        `when`(locationClient.getLocationUpdates(1000L)).thenReturn(flowOf(location))
        `when`(locationApiRepository.updateLocation(location)).thenReturn(Result.Success(
            LocationUpdateResult("Success")
        ))

        var updatedLocation: Pair<Double, Double>? = null
        locationUpdateOnceUseCase.updateLocationOnce({ lat, lon ->
            updatedLocation = Pair(lat, lon)
        }, { _, _ -> })

        assert(updatedLocation == Pair(location.first, location.second))
    }

    @Test
    fun `updateLocationOnce returns error on failure`() = runTest {
        val location = Pair(1.0, 1.0)
        `when`(locationClient.getLocationUpdates(1000L)).thenReturn(flowOf(location))
        `when`(locationApiRepository.updateLocation(location)).thenReturn(Result.Failure(500, "Error"))

        var error: Pair<Int, String>? = null
        locationUpdateOnceUseCase.updateLocationOnce({ _, _ -> }, { code, message ->
            error = Pair(code, message)
        })

        assert(error == Pair(500, "Error"))
    }

    @Test
    fun `updateLocationOnce returns error on exception`() = runTest {
        `when`(locationClient.getLocationUpdates(1000L)).thenThrow(RuntimeException("Error"))

        var error: Pair<Int, String>? = null
        locationUpdateOnceUseCase.updateLocationOnce({ _, _ -> }, { code, message ->
            error = Pair(code, message)
        })

        assert(error == Pair(-1, "Error"))
    }
}