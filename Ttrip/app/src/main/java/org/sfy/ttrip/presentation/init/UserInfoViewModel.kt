package org.sfy.ttrip.presentation.init

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.sfy.ttrip.data.remote.Resource
import org.sfy.ttrip.data.remote.repository.CheckDuplicationResponse
import org.sfy.ttrip.domain.usecase.user.CheckDuplicationUseCase
import org.sfy.ttrip.domain.usecase.user.PatchUserInfoUseCase
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val checkDuplicationUseCase: CheckDuplicationUseCase,
    private val patchUserInfoUseCase: PatchUserInfoUseCase
) : ViewModel() {

    private val _isDuplicate: MutableLiveData<Boolean?> = MutableLiveData(null)
    val isDuplicate: MutableLiveData<Boolean?> = _isDuplicate

    private val _nickname: MutableLiveData<String> = MutableLiveData(null)
    val nickname: MutableLiveData<String> = _nickname

    private val _userAge: MutableLiveData<String?> = MutableLiveData("")
    val userAge: LiveData<String?> = _userAge

    private val _userSex: MutableLiveData<String?> = MutableLiveData("")
    val userSex: LiveData<String?> = _userSex

    private val _userIntro: MutableLiveData<String?> = MutableLiveData()
    val userIntro: LiveData<String?> = _userIntro

    private val _profileImgUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val profileImgUri: MutableLiveData<Uri?> = _profileImgUri

    private var profileImgMultiPart: MultipartBody.Part? = null

    suspend fun checkDuplication() =
        viewModelScope.async {
            when (val value = checkDuplicationUseCase(nickname.value!!)) {
                is Resource.Success<CheckDuplicationResponse> -> {
                    _isDuplicate.value = value.data.isExist
                    return@async 1
                }
                is Resource.Error -> {
                    Log.e("checkDuplication", "checkDuplication: ${value.errorMessage}")
                    return@async 0
                }
            }
        }.await()

    fun patchUserInfo() =
        viewModelScope.launch {
            patchUserInfoUseCase(
                nickname.value!!,
                userIntro.value!!,
                userSex.value!!,
                profileImgMultiPart,
                profileImgMultiPart,
                userAge.value!!,
                ""
            )
        }

    fun returnDuplicationTrue() {
        _isDuplicate.value = true
    }

    fun postAge(age: String?) {
        _userAge.value = age
    }

    fun postSex(sex: String?) {
        _userSex.value = sex
    }

    fun postIntro(intro: String?) {
        _userIntro.value = intro
    }

    fun setProfileImg(uri: Uri, file: File) {
        viewModelScope.launch {
            _profileImgUri.value = uri
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            profileImgMultiPart =
                MultipartBody.Part.createFormData("profileImg", file.name, requestFile)
        }
    }
}