package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.repositories.GroupRepository

class GroupViewModel: ViewModel() {
    private val query = mutableLiveData("")
    private val userItems = mutableLiveData(loadUsers())
    private val groupRepository = GroupRepository
    private val selectedItems = Transformations.map(userItems) {users -> users.filter {it.isSelected}}

    fun getUsersData(): LiveData<List<UserItem>> {
        val result= MediatorLiveData<List<UserItem>>() // Получает большое число источников и подписывается на их изменение

        val filterF = {
            Log.d("M_GroupViewModel","query= ${query.value}" )
            val queryStr = query.value!!
            val users = userItems.value!!

            result.value = if (queryStr.isEmpty()) users // Если Query пуст возвращаем всех пользователей
            else users.filter { it.fullName.contains(queryStr, true) } // Иначе отобранных по включению строки

        }

        result.addSource(userItems) { filterF.invoke()  } // Подписываемся на UserItems и ставим лямбду, вызываемую каждый раз при изменении данных
        result.addSource(query) { filterF.invoke() }
        return result
    }

    fun getSelectedData():LiveData<List<UserItem>> = selectedItems

    fun handleSelectedItem(userId:String) {
        userItems.value = userItems.value!!.map{
            if(it.id==userId) it.copy(isSelected = !it.isSelected)
            else it
        }
    }

    fun handleRemoveChip(userId: String) {
        userItems.value = userItems.value!!.map{
            if(it.id == userId) it.copy(isSelected = false )
            else it
        }
    }

    fun handleSearchQuery(text: String) { // Медиатор
        query.value = text
    }

    private fun loadUsers(): List<UserItem>  = GroupRepository.loadUsers().map{it.toUserItem()}

    fun handleCreateGroup() {
        groupRepository.createChat(selectedItems.value!!)
    }
}