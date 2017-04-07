package models.exceptions

/**
  * Created by james-forster on 07/04/17.
  */

trait AuthException extends Exception

class UserNotFoundException extends AuthException
class DuplicateUserException extends AuthException
class InvalidTokenException extends AuthException
class TokenTimeoutException extends AuthException
class IncorrectPasswordException extends AuthException
