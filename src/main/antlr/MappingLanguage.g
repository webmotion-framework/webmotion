grammar MappingLanguage;

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
    : section_config (section_error | section_filter | section_action | comment)*
    ;

// Section config

section_config
    : section_config_label Newline (section_config_rule Newline)*
    ;

section_config_label
    : '[config]'
    ;

section_config_rule
    : section_config_name '=' section_config_value
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
    : ('.' | '/' | Letter)*
    ;

// Section error

section_error
    : section_error_label Newline (section_error_rule Newline)*
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
    : 'code:' | (Digit Digit Digit)
    ;

// Section filter

section_filter
    : section_filter_label Newline (section_filter_rule Newline)*
    ;

section_filter_label
    : '[filters]'
    ;

section_filter_rule
    : Method Blank section_filter_path Blank action
    ;

section_filter_path
    : ('/' (Letter* | '*'))+
    ;

// Section action

section_action
    : section_action_label Newline (section_action_rule Newline)*
    ;

section_action_label
    : '[actions]'
    ;

section_action_rule
    : Method Blank section_action_path Blank (view | url | section_action_dynamic) (Blank section_action_default_parameters)?
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
    : Letter+ '=' (section_action_variable | Letter*)
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
    : Letter+ '=' Letter*
    ;

// Comment
comment
    : '#' (Letter | Blank)* Newline
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

// TODO: jru 20110803 add other char in url
url
    : 'url:' (Letter)*
    ;

// TODO: jru 20110803 add other char in pattern
pattern
    : (Letter | '?' | '*' | '^' | '$')+
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

Method
    : '*'
    | 'GET'
    | 'get'
    | 'POST'
    | 'post'
    | 'HEAD'
    | 'head'
    | 'PUT'
    | 'put'
    | 'DELETE'
    | 'delete'
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
