parser grammar MappingLanguageParser;

options {
    output=AST;
    ASTLabelType=CommonTree;
}

@header {
    package org.debux.webmotion.server.mapping;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
}

@members {
    private static final Logger log = LoggerFactory.getLogger(MappingLanguageParser.class);

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
    -> section_config_rule+
    ;

section_config_rule
    : section_config_name section_config_value
    -> ^(SECTION_CONFIG section_config_name section_config_value)
    ;

section_config_value
    :  EQUALS .*
    -> DOLAR[$text]
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
    -> section_error_rule+
    ;

section_error_rule
    : (full_name | section_error_code) Blank (view | url | action)
    -> ^(SECTION_ERRORS full_name* section_error_code* view* url* action*)
    ;

section_error_code
    : CODE Digit Digit Digit
    -> SECTION_ERRORS[$text]
    ;

// Section filter

section_filter
    : SECTION_FILTERS Newline ((section_filter_rule Newline) | comment)*
    -> section_filter_rule+
    ;

section_filter_rule
    : method Blank section_filter_path Blank action
    -> ^(SECTION_FILTERS method ^(DOLAR["PATH"] section_filter_path) action)
    ;

section_filter_path
    : (SLASH (simple_name | ASTERISK))+ SLASH?
    ;

// Section action

section_action
    : SECTION_ACTIONS Newline ((section_action_rule Newline) | comment)*
    -> section_action_rule+
    ;

section_action_rule
    : method Blank section_action_path Blank (view | url | section_action_dynamic) (Blank section_action_default_parameters)?
    -> ^(SECTION_ACTIONS method ^(DOLAR["PATH"] section_action_path) view* url* section_action_dynamic* section_action_default_parameters*)
    ;

section_action_path
    : (SLASH (simple_name | section_action_variable)? )+ section_action_path_parameters?
    ;

section_action_variable
    : LEFT_CURLY_BRACE simple_name (COLON pattern)? RIGHT_CURLY_BRACE
    -> ^(DOLAR["VARIABLE"] simple_name pattern*)
    ;

section_action_path_parameters
    : QUESTION_MARK section_action_parameters
    -> ^(DOLAR["PARAMETERS"] section_action_parameters)
    ;

section_action_parameters
    :  (section_action_parameter (AMPERSAND section_action_parameter)*)?
    -> section_action_parameter*
    ;

section_action_parameter
    : name=simple_name (EQUALS (section_action_variable | value=simple_name)? )?
    -> ^($name section_action_variable* $value*)
    ;
    
section_action_dynamic
    : ACTION? f=section_action_dynamic_variable (PERIOD n=section_action_dynamic_variable)+
    -> ^(DOLAR["ACTION"] $f (PERIOD $n)+)
    ;

section_action_dynamic_variable
    : (LEFT_CURLY_BRACE simple_name RIGHT_CURLY_BRACE)+ -> ^(DOLAR["VARIABLE"] simple_name)+
    | (Letter | Digit)+ -> DOLAR[$text]
    ;

section_action_default_parameters
    :  (section_action_default_parameter (COMMA section_action_default_parameter)*)?
    -> section_action_default_parameter*
    ;

section_action_default_parameter
    : simple_name section_action_default_parameter_value
    -> ^(DOLAR["DEFAULT_PARAMETERS"] simple_name section_action_default_parameter_value)
    ;

section_action_default_parameter_value
    : EQUALS .*
    -> DOLAR[$text]
    ;

// Comment
comment
    : NUMBER_SIGN .* Newline
    ;

// Common

action
    : ACTION? full_name
    -> ^(DOLAR["ACTION"] full_name)
    ;

view
    : VIEW simple_name COLON full_name
    -> ^(DOLAR["VIEW"]  simple_name full_name)
    ;

url
    : URL .*
    -> ^(DOLAR["URL"]  DOLAR[$text])
    ;

// TODO: jru 20110803 add other char in pattern
pattern
    : (Letter | Digit | QUESTION_MARK | ASTERISK | CIRCUMFLEX_ACCENT | DOLAR)+
    -> DOLAR[$text]
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
    -> DOLAR[$text]
    ;

full_name
    : f=simple_name (PERIOD n=simple_name)*
    -> $f (PERIOD $n)*
    ;

dummy
    : HYPHEN | BACKSLASH
    -> DOLAR[$text]
    ;
