package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.model.AuthResult
import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.utils.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AuthUseCaseTest {

    @Mock
    private lateinit var locationApiRepository: LocationApiRepository

    private lateinit var authUseCase: AuthUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authUseCase = AuthUseCase(locationApiRepository)
    }

    @Test
    fun `auth returns success result`() = runBlocking {
        val apiKey = "testApiKey"
        val expected = Result.Success(AuthResult("testToken","abc","refreshToken"))
        `when`(locationApiRepository.auth(apiKey)).thenReturn(expected)

        val actual = authUseCase.auth(apiKey)

        assertEquals(expected, actual)
    }

    @Test
    fun `auth returns error result`() = runBlocking {
        val apiKey = "testApiKey"
        val expected = Result.Failure(403,"Something went wrong")
        `when`(locationApiRepository.auth(apiKey)).thenReturn(expected)

        val actual = authUseCase.auth(apiKey)

        assertEquals(expected, actual)
    }
}
