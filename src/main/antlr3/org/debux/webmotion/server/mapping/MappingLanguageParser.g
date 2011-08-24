parser grammar MappingLanguageParser;

options {
    output=AST;
    ASTLabelType=CommonTree;
}

@header {
    package org.debux.webmotion.server.mapping;
}

@members {
    protected IErrorReporter errorReporter = null;
    public void setErrorReporter(IErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }
    public void emitErrorMessage(String msg) {
        errorReporter.reportError(msg);
    }
}

mapping
    : comment* section_config (section_error | section_filter | section_action)* EOF
    ;

// Section config

section_config
    : SECTION_CONFIG Newline ((section_config_rule Newline) | comment)*
    ;

section_config_rule
    : section_config_name EQUALS .*
    ;

section_config_name
    : CONFIG_PACKAGE_VIEWS
    | CONFIG_PACKAGE_FILTERS
    | CONFIG_PACKAGE_ACTIONS
    | CONFIG_PACKAGE_ERRORS
    | CONFIG_RELOADABLE
    | CONFIG_MODE
    | CONFIG_REQUEST_ENCODING
    ;

// Section error

section_error
    : SECTION_ERRORS Newline ((section_error_rule Newline) | comment)*
    ;

section_error_rule
    : (full_name | section_error_code) Blank (view | url | action)
    ;

section_error_code
    : CODE Digit Digit Digit
    ;

// Section filter

section_filter
    : SECTION_FILTERS Newline ((section_filter_rule Newline) | comment)*
    ;

section_filter_rule
    : method Blank section_filter_path Blank action 
    ;

section_filter_path
    : (SLASH (simple_name | ASTERISK))+ SLASH?
    ;

// Section action

section_action
    : SECTION_ACTIONS Newline ((section_action_rule Newline) | comment)*
    ;

section_action_rule
    : method Blank section_action_path Blank (view | url | section_action_dynamic) (Blank section_action_default_parameters)?
    ;

section_action_path
    : (SLASH (simple_name | section_action_variable)? )+ (QUESTION_MARK section_action_parameters)?
    ;

section_action_variable
    : LEFT_CURLY_BRACE simple_name (COLON pattern)? RIGHT_CURLY_BRACE
    ;

section_action_parameters
    :  (section_action_parameter (AMPERSAND section_action_parameter)*)?
    ;

section_action_parameter
    : simple_name (EQUALS (section_action_variable | simple_name)? )?
    ;
    
section_action_dynamic
    : ACTION? section_action_dynamic_variable (PERIOD section_action_dynamic_variable)+
    ;

section_action_dynamic_variable
    : (LEFT_CURLY_BRACE simple_name RIGHT_CURLY_BRACE | Letter | Digit)+
    ;

section_action_default_parameters
    :  (section_action_default_parameter (COMMA section_action_default_parameter)*)?
    ;

section_action_default_parameter
    : simple_name EQUALS .*
    ;

// Comment
comment
    : NUMBER_SIGN .* Newline
    ;

// Common

action
    : ACTION? full_name
    ;

view
    : VIEW simple_name COLON full_name
    ;

url
    : URL .*
    ;

// TODO: jru 20110803 add other char in pattern
pattern
    : (Letter | Digit | QUESTION_MARK | ASTERISK | CIRCUMFLEX_ACCENT | DOLAR)+
    ;

method
    : METHOD_GET
    | METHOD_POST
    | METHOD_HEAD
    | METHOD_PUT
    | METHOD_DELETE
    | ASTERISK
    ;

simple_name
    : (Letter | Digit)+
    ;

full_name
    : simple_name (PERIOD simple_name)*
    ;
