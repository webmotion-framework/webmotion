lexer grammar MappingLanguageLexer;

options {
    filter=true;
}

@lexer::header {
    package org.debux.webmotion.server.mapping;
}

@lexer::members {
    protected IErrorReporter errorReporter = null;
    public void setErrorReporter(IErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }
    public void emitErrorMessage(String msg) {
        errorReporter.reportError(msg);
    }
}

SECTION_CONFIG:'[config]';
SECTION_ERRORS:'[errors]';
SECTION_FILTERS:'[filters]';
SECTION_ACTIONS:'[actions]';

CONFIG_PACKAGE_VIEWS:'package.views';
CONFIG_PACKAGE_FILTERS:'package.filters';
CONFIG_PACKAGE_ACTIONS:'package.actions';
CONFIG_PACKAGE_ERRORS:'package.errors';
CONFIG_RELOADABLE:'reloadable';
CONFIG_MODE:'mode';
CONFIG_REQUEST_ENCODING:'request.encoding';

ACTION:'action:';
CODE:'code:';
VIEW:'view.';
URL:'url:';

METHOD_GET:'GET';
METHOD_POST:'POST';
METHOD_HEAD:'HEAD';
METHOD_PUT:'PUT';
METHOD_DELETE:'DELETE';

HYPHEN:'-';
SLASH:'/';
BACKSLASH:'\\';
EQUALS:'=';
ASTERISK:'*';
COMMA:',';
CIRCUMFLEX_ACCENT:'^';
DOLAR:'$';
NUMBER_SIGN:'#';
LEFT_CURLY_BRACE:'{';
RIGHT_CURLY_BRACE:'}';
PERIOD:'.';
QUESTION_MARK:'?';
COLON:':';
AMPERSAND:'&';

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
    {skip();}
    ;

Blank
    : (' '|'\t')+
    {skip();}
    ;
