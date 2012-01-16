lexer grammar MappingLanguageLexer;

options {
    filter=true;
}

@lexer::header {
    package org.debux.webmotion.server.parser;
}

@lexer::members {
    protected ErrorReporter errorReporter = null;
    public void setErrorReporter(ErrorReporter errorReporter) {
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
SECTION_EXTENSIONS:'[extensions]';

CONFIG_PACKAGE_VIEWS:'package.views';
CONFIG_PACKAGE_BASE:'package.base';
CONFIG_PACKAGE_FILTERS:'package.filters';
CONFIG_PACKAGE_ACTIONS:'package.actions';
CONFIG_PACKAGE_ERRORS:'package.errors';
CONFIG_JAVAC_DEBUG:'javac.debug';
CONFIG_MODE:'mode';
CONFIG_REQUEST_ENCODING:'request.encoding';
CONFIG_REQUEST_ASYNC:'request.async';
HANDLERS_FACTORY_CLASS:'handlers.factory.class';

ACTION:'action:';
ACTION_ASYNC:'async:';
ACTION_SYNC:'sync:';
CODE:'code:';
VIEW:'view:';
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
PLUS:'+';
ASTERISK:'*';
COMMA:',';
CIRCUMFLEX_ACCENT:'^';
DOLLAR:'$';
NUMBER_SIGN:'#';
LEFT_PARENTHESIS:'(';
RIGHT_PARENTHESIS:')';
LEFT_CURLY_BRACE:'{';
RIGHT_CURLY_BRACE:'}';
LEFT_SQUARE_BRACKET:'[';
RIGHT_SQUARE_BRACKET:']';
DOT:'.';
QUESTION_MARK:'?';
COLON:':';
AMPERSAND:'&';
//PERCENT:'%';

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
