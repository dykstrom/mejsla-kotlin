package se.dykstrom.spring.coroutines.inspection

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import se.dykstrom.spring.coroutines.address.AddressNotFoundException

/**
 * Interface that contains functions for handling exceptions thrown by the controller classes.
 */
interface BaseExceptionHandler {

    @ExceptionHandler
    fun handleAddressNotFound(e: AddressNotFoundException)= notFound("Address not found")

    @ExceptionHandler
    fun handleInspectionNotFound(e: InspectionNotFoundException) = notFound("Inspection not found")

    private fun notFound(msg: String) = ResponseEntity("{\"message\":\"$msg\"}", HttpStatus.NOT_FOUND)
}
