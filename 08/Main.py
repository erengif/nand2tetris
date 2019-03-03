#!/usr/bin/env python3
import sys
import glob
from CodeWriter import CodeWriter
from Parser import Parser

def translate(code, filename):
    parse = Parser(filename)
    code.set_static()
    while parse.has_more_commands():
        parse.advance()
        if parse.command_type() == "push" or parse.command_type() == "pop":
            code.write_push_pop(parse.command_type(), parse.arg1(), parse.arg2())
        elif parse.command_type() == "label":
            code.write_label(parse.arg1())
        elif parse.command_type() == "if-goto":
            code.write_if(parse.arg1())
        elif parse.command_type() == "goto":
            code.write_goto(parse.arg1())
        elif parse.command_type() == "function":
            code.write_function(parse.arg1(), int(parse.arg2()))
        elif parse.command_type() == "call":
            code.write_call(parse.arg1(), parse.arg2())
        elif parse.command_type() == "return":
            code.write_return()
        elif len(parse.current_inst) == 1:
            code.write_arithmetic(parse.command_type())
        else:
            print("Command not found '{1}', Line {0} ".format(parse.i + 1, parse.command_type()))
            return
def main():
    foldername = str(sys.argv[-1])
    code = CodeWriter()
    if foldername[-2:] == 'vm':
        translate(code,foldername)
    else:
        if '{0}Sys.vm'.format(foldername) in glob.glob(foldername+'*.vm'):
            code.write_init()
        for filename in glob.glob(foldername+'*.vm'):
            translate(code,filename)
    code.create_file(foldername)
    print("Done!")
main()