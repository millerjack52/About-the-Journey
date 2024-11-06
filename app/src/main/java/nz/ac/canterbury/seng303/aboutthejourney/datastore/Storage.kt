package nz.ac.canterbury.seng303.aboutthejourney.datastore;

import kotlinx.coroutines.flow.Flow
import nz.ac.canterbury.seng303.aboutthejourney.models.Identifiable

/**
 * Interface for a storage class
 * Acknowledgement: This code is based on the Storage interface from the SENG 303 Lab 2 solution
 */
interface Storage<T> where T : Identifiable {
    fun get(where: (T) -> Boolean): Flow<T>
    fun getAll(): Flow<List<T>>
    fun insert(data: T): Flow<Int>
    fun insertAll(data: List<T>): Flow<Int>
    fun edit(identifier: Int, data: T): Flow<Int>
    fun delete(identifier: Int): Flow<Int>
}