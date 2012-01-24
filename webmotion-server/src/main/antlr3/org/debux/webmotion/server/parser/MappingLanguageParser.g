parser grammar MappingLanguageParser;

options {
    output=AST;
    ASTLabelType=CommonTree;
}

@header {
    package org.debux.webmotion.server.parser;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
}

@members {
    private static final Logger log = LoggerFactory.getLogger(MappingLanguageParser.class);

    protected ErrorReporter errorReporter = null;
    public void setErrorReporter(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }
    public void emitErrorMessage(String msg) {
        errorReporter.reportError(msg);
    }
}

mapping
    : comment* section_config? (section_error | section_filter | section_action | section_extension)* EOF
    ;

// Section config

section_config
    : SECTION_CONFIG Newline ((section_config_rule Newline) | comment)*
    -> section_config_rule*
    ;

section_config_rule
    : section_config_name section_config_value
    -> ^(DOLLAR["CONFIG"] ^(DOLLAR["NAME"] section_config_name) ^(DOLLAR["VALUE"] section_config_value))
    ;

section_config_value
    :  EQUALS .*
    -> DOT[$text]
    ;

section_config_name
    : CONFIG_PACKAGE_VIEWS
    | CONFIG_PACKAGE_BASE
    | CONFIG_PACKAGE_FILTERS
    | CONFIG_PACKAGE_ACTIONS
    | CONFIG_PACKAGE_ERRORS
    | CONFIG_JAVAC_DEBUG
    | CONFIG_REQUEST_ENCODING
    | CONFIG_REQUEST_ASYNC
    | CONFIG_HANDLERS_FACTORY_CLASS
    | CONFIG_SERVER_ERROR_PAGE
    | CONFIG_SERVER_LISTENER_CLASS
    | CONFIG_SERVER_CONTROLLER_SCOPE
    ;

// Section error

section_error
    : SECTION_ERRORS Newline ((section_error_rule Newline) | comment)*
    -> section_error_rule*
    ;

section_error_rule
    : (full_name | section_error_code | ASTERISK) Blank (view | url | action)
    -> ^(DOLLAR["ERROR"] ^(DOLLAR["EXCEPTION"] full_name)? section_error_code? view? url? action?)
    ;

section_error_code
    : CODE section_error_code_value
    -> ^(DOLLAR["CODE"] section_error_code_value)
    ;

section_error_code_value
    : Digit Digit Digit
    -> DOT[$text]
    ;

// Section filter

section_filter
    : SECTION_FILTERS Newline ((section_filter_rule Newline) | comment)*
    -> section_filter_rule*
    ;

section_filter_rule
    : method Blank section_filter_path Blank action
    -> ^(DOLLAR["FILTER"] ^(DOLLAR["METHOD"] method) ^(DOLLAR["PATH"] section_filter_path) action)
    ;

section_filter_path
    : (SLASH (path_name | ASTERISK))+ SLASH?
    -> DOT[$text]
    ;

// Section action

section_action
    : SECTION_ACTIONS Newline ((section_action_rule Newline) | comment)*
    -> section_action_rule*
    ;

section_action_rule
    : method Blank section_action_path section_action_path_parameters? Blank (dynamic_view | dynamic_url | dynamic_action) (Blank section_action_default_parameters)?
    -> ^(DOLLAR["ACTION"] ^(DOLLAR["METHOD"] method) section_action_path section_action_path_parameters? dynamic_view? dynamic_url? dynamic_action? section_action_default_parameters*)
    ;

section_action_path
    : section_action_path_value+
    -> ^(DOLLAR["PATH"] section_action_path_value*)
    ;

section_action_path_value
    : SLASH (path_name | section_action_variable)?
    ;

section_action_variable
    : LEFT_CURLY_BRACE full_name? (COLON pattern)? RIGHT_CURLY_BRACE
    -> ^(DOLLAR["VARIABLE"] full_name? ^(DOLLAR["PATTERN"] pattern)?)
    ;

section_action_path_parameters
    : QUESTION_MARK section_action_parameters
    -> ^(DOLLAR["PARAMETERS"] section_action_parameters)
    ;

section_action_parameters
    :  (section_action_parameter (AMPERSAND section_action_parameter)*)?
    -> section_action_parameter*
    ;

section_action_parameter
    : name=full_name (EQUALS (section_action_variable | value=simple_name)? )?
    -> ^(DOLLAR["PARAMETER"] ^(DOLLAR["NAME"] $name) section_action_variable* ^(DOLLAR["VALUE"] $value)?)
    ;
    
section_action_default_parameters
    :  (section_action_default_parameter (COMMA section_action_default_parameter)*)?
    -> ^(DOLLAR["DEFAULT_PARAMETERS"] section_action_default_parameter*)
    ;

section_action_default_parameter
    : full_name section_action_default_parameter_value
    -> ^(DOLLAR["PARAMETER"] ^(DOLLAR["NAME"] full_name) ^(DOLLAR["VALUE"] section_action_default_parameter_value))
    ;

section_action_default_parameter_value
    : EQUALS .*
    -> DOT[$text]
    ;

// Section extension

section_extension
    : SECTION_EXTENSIONS Newline ((section_extension_rule Newline) | comment)*
    -> section_extension_rule*
    ;

section_extension_rule
    : section_extension_path Blank file_name
    -> ^(DOLLAR["EXTENSION"] ^(DOLLAR["PATH"] section_extension_path) ^(DOLLAR["FILE"] file_name))
    ;

section_extension_path
    : SLASH | (SLASH path_name)+
    -> DOT[$text]
    ;

// Comment
comment
    : NUMBER_SIGN .* Newline
    ;

// Common

action
    : (ACTION | ACTION_ASYNC | ACTION_SYNC)? full_name
    -> ^(DOLLAR["ACTION"] ^(DOLLAR["TYPE"] ACTION_ASYNC? ACTION_SYNC?) full_name)
    ;

view
    : VIEW file_name
    -> ^(DOLLAR["VIEW"] file_name)
    ;

url
    : URL href
    -> ^(DOLLAR["URL"] href)
    ;

dynamic_variable
    : simple_name RIGHT_CURLY_BRACE
    ;

dynamic_action
    : (ACTION | ACTION_ASYNC | ACTION_SYNC)? dynamic_full_name
    -> ^(DOLLAR["ACTION"] ^(DOLLAR["TYPE"] ACTION_ASYNC? ACTION_SYNC?) dynamic_full_name)
    ;

dynamic_full_name
    : dynamic_simple_name (DOT dynamic_simple_name)*
    -> DOT[$text]
    ;

dynamic_simple_name
    : (LEFT_CURLY_BRACE dynamic_variable | Letter | Digit)+
    ;

dynamic_view
    : VIEW dynamic_file_name
    -> ^(DOLLAR["VIEW"] dynamic_file_name)
    ;

dynamic_file_name
    : (LEFT_CURLY_BRACE dynamic_variable | SLASH | Letter | Digit | DOT | HYPHEN)+
    -> DOT[$text]
    ;

dynamic_url
    : URL dynamic_href
    -> ^(DOLLAR["URL"] dynamic_href)
    ;

dynamic_href
    : (LEFT_CURLY_BRACE dynamic_variable | Letter | Digit | DOT | HYPHEN | SLASH | BACKSLASH | EQUALS | PLUS 
    | ASTERISK | COMMA | CIRCUMFLEX_ACCENT | DOLLAR | NUMBER_SIGN | LEFT_PARENTHESIS 
    | RIGHT_PARENTHESIS | QUESTION_MARK | AMPERSAND | COLON | LEFT_SQUARE_BRACKET 
    | RIGHT_SQUARE_BRACKET)+
    -> DOT[$text]
    ;

pattern
    : (Letter| Digit | QUESTION_MARK | ASTERISK | CIRCUMFLEX_ACCENT 
    | DOLLAR | PLUS | LEFT_SQUARE_BRACKET | RIGHT_SQUARE_BRACKET | HYPHEN 
    | DOT |LEFT_PARENTHESIS| RIGHT_PARENTHESIS| BACKSLASH | SLASH)+
    -> DOT[$text]
    ;

method
    : method_http (COMMA method_http)*
    -> method_http*
    | ASTERISK
    -> ASTERISK
    ;

method_http
    : METHOD_GET
    | METHOD_POST
    | METHOD_HEAD
    | METHOD_PUT
    | METHOD_DELETE
    ;

path_name
    : (Letter | Digit | HYPHEN)+
    -> DOT[$text]
    ;

simple_name
    : (Letter | Digit)+
    -> DOT[$text]
    ;

full_name
    : simple_name (DOT simple_name)*
    -> DOT[$text]
    ;

file_name
    : (SLASH | Letter | Digit | DOT | HYPHEN)+
    -> DOT[$text]
    ;

href
    : (Letter | Digit | DOT | HYPHEN | SLASH | BACKSLASH | EQUALS | PLUS 
    | ASTERISK | COMMA | CIRCUMFLEX_ACCENT | DOLLAR | NUMBER_SIGN | LEFT_PARENTHESIS 
    | RIGHT_PARENTHESIS | QUESTION_MARK | AMPERSAND | COLON | LEFT_SQUARE_BRACKET 
    | RIGHT_SQUARE_BRACKET)+
    -> DOT[$text]
    ;
