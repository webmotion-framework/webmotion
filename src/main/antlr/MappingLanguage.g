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
    : section_config_name '=' section_config_value?
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

section_config_value
    : ('/' | Letter+) (('.' | '/') Letter+)*
    ;

// Section error

section_error
    : section_error_label Newline ((section_error_rule Newline) | comment)*
    ;

section_error_label
    : '[errors]'
    ;

section_error_rule
    : (section_error_exception | section_error_code) Blank (view | url | action)
    ;

section_error_exception
    : Letter+ ('.' (Letter)+)*
    ;

section_error_code
    : 'code:' (Digit Digit Digit)
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
    : ('/' (Letter+ | '*'))+
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
    : ('/' (Letter* | section_action_variable))+ ('?' section_action_parameters)?
    ;

section_action_variable
    : '{' Letter+ (':' pattern)? '}'
    ;

section_action_parameters
    :  (section_action_parameter ('&' section_action_parameter)*)?
    ;

section_action_parameter
    : Letter+ ('=' (section_action_variable | Letter*))?
    ;
    
section_action_dynamic
    : 'action:'? section_action_dynamic_variable ('.' section_action_dynamic_variable)+
    ;

section_action_dynamic_variable
    : ('{' Letter+ '}' | Letter)+
    ;

section_action_default_parameters
    :  (section_action_default_parameter (',' section_action_default_parameter)*)?
    ;

section_action_default_parameter
    : Letter+ '=' .*
    ;

// Comment
comment
    : '#' .* Newline
    ;

// Common

action
    : 'action:'? name
    ;

view
    : 'view.' (Letter)+ ':' name
    ;

name
    : (Letter)+ ('.' (Letter)+)+
    ;

url
    : 'url:' .*
    ;

// TODO: jru 20110803 add other char in pattern
pattern
    : (Letter | '?' | '*' | '^' | '$')+
    ;

method
    : 'GET'
    | 'get'
    | 'POST'
    | 'post'
    | 'HEAD'
    | 'head'
    | 'PUT'
    | 'put'
    | 'DELETE'
    | 'delete'
    | '*'
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
