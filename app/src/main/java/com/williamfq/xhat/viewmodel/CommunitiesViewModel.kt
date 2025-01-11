// Archivo: com/williamfq/xhat/presentation/viewmodel/CommunitiesViewModel.kt
package com.williamfq.xhat.viewmodel

import androidx.lifecycle.*
import com.williamfq.domain.model.Community
import com.williamfq.domain.model.CommunityResult
import com.williamfq.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunitiesViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _communitiesState = MutableLiveData<CommunityResult>()
    val communitiesState: LiveData<CommunityResult> = _communitiesState

    init {
        loadCommunities()
    }

    fun loadCommunities() {
        viewModelScope.launch {
            _communitiesState.value = CommunityResult.Loading
            try {
                // Colectar el flujo de comunidades
                communityRepository.getCommunities().collect { communities ->
                    _communitiesState.value = CommunityResult.Success(communities)
                }
            } catch (e: Exception) {
                _communitiesState.value = CommunityResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun searchCommunities(query: String) {
        viewModelScope.launch {
            _communitiesState.value = CommunityResult.Loading
            try {
                // Implementa el método searchCommunities en el repositorio si aún no lo tienes
                communityRepository.searchCommunities(query).collect { communities ->
                    _communitiesState.value = CommunityResult.Success(communities)
                }
            } catch (e: Exception) {
                _communitiesState.value = CommunityResult.Error(e.message ?: "Error en la búsqueda")
            }
        }
    }

    fun createCommunity(community: Community) {
        viewModelScope.launch {
            _communitiesState.value = CommunityResult.Loading
            try {
                communityRepository.createCommunity(community)
                loadCommunities() // Recargar la lista después de crear
            } catch (e: Exception) {
                _communitiesState.value = CommunityResult.Error(e.message ?: "Error al crear la comunidad")
            }
        }
    }

    fun subscribeToCommunity(communityId: String) {
        viewModelScope.launch {
            try {
                communityRepository.subscribeCommunity(communityId)
                loadCommunities() // Recargar la lista después de suscribirse
            } catch (e: Exception) {
                _communitiesState.value = CommunityResult.Error(e.message ?: "Error al suscribirse")
            }
        }
    }
}
