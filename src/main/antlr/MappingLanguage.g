grammar MappingLanguage;

options {
    filter=true;
}

@header {
    package org.debux.webmotion.server.mapping;
}

@lexer::header {
    package org.debux.webmotion.server.mapping;
}

@members {
    private IErrorReporter errorReporter = null;
    public void setErrorReporter(IErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }
    public void emitErrorMessage(String msg) {
        errorReporter.reportError(msg);
    }
}

@lexer::members {
    private IErrorReporter errorReporter = null;
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
    : section_config_label Newline ((section_config_rule Newline) | comment)*
    ;

section_config_label
    : '[config]'
    ;

section_config_rule
    : section_config_name '=' .*
    ;

section_config_name
    : 'package.views'
    | 'package.filters'
    | 'package.actions'
    | 'package.errors'
    | 'reloadable'
    | 'mode'
    | 'request.encoding'
    ;

// Section error

section_error
    : section_error_label Newline ((section_error_rule Newline) | comment)*
    ;

section_error_label
    : '[errors]'
    ;

section_error_rule
    : (full_name | section_error_code) Blank (view | url | action)
    ;

section_error_code
    : 'code:' Digit Digit Digit
    ;

// Section filter

section_filter
    : section_filter_label Newline ((section_filter_rule Newline) | comment)*
    ;

section_filter_label
    : '[filters]'
    ;

section_filter_rule
    : method Blank section_filter_path Blank action
    ;

section_filter_path
    : ('/' (simple_name | '*'))+ '/'?
    ;

// Section action

section_action
    : section_action_label Newline ((section_action_rule Newline) | comment)*
    ;

section_action_label
    : '[actions]'
    ;

section_action_rule
    : method Blank section_action_path Blank (view | url | section_action_dynamic) (Blank section_action_default_parameters)?
    ;

section_action_path
    : ('/' (simple_name | section_action_variable)? )+ ('?' section_action_parameters)?
    ;

section_action_variable
    : '{' simple_name (':' pattern)? '}'
    ;

section_action_parameters
    :  (section_action_parameter ('&' section_action_parameter)*)?
    ;

section_action_parameter
    : simple_name ('=' (section_action_variable | simple_name)? )?
    ;
    
section_action_dynamic
    : 'action:'? section_action_dynamic_variable ('.' section_action_dynamic_variable)+
    ;

section_action_dynamic_variable
    : ('{' simple_name '}' | Letter | Digit)+
    ;

section_action_default_parameters
    :  (section_action_default_parameter (',' section_action_default_parameter)*)?
    ;

section_action_default_parameter
    : simple_name '=' .*
    ;

// Comment
comment
    : '#' .* Newline
    ;

// Common

action
    : 'action:'? full_name
    ;

view
    : 'view.' simple_name ':' full_name
    ;

url
    : 'url:' .*
    ;

// TODO: jru 20110803 add other char in pattern
pattern
    : (Letter | Digit | '?' | '*' | '^' | '$')+
    ;

method
    : 'GET'
    | 'POST'
    | 'HEAD'
    | 'PUT'
    | 'DELETE'
    | '*'
    ;

simple_name
    : (Letter | Digit)+
    ;

full_name
    : simple_name ('.' simple_name)*
    ;

Letter
    : '\u0024'
    | '\u0041'..'\u005a'
    | '\u005f'
    | '\u0061'..'\u007a'
    | '\u00c0'..'\u00d6'
    | '\u00d8'..'\u00f6'
    | '\u00f8'..'\u00ff'
    | '\u0100'..'\u1fff'
    | '\u3040'..'\u318f'
    | '\u3300'..'\u337f'
    | '\u3400'..'\u3d2d'
    | '\u4e00'..'\u9fff'
    | '\uf900'..'\ufaff'
    ;

Digit
    : '0'..'9'
    ;

Newline
    : ((' '|'\t')* '\r'? '\n')+
    ;

Blank
    : (' '|'\t')+
    ;
