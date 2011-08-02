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

utterance : greeting | exclamation;
greeting : interjection subject;
exclamation : 'Hooray!';
interjection : 'Hello';
subject : 'World!';
