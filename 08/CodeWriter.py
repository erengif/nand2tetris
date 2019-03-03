    class CodeWriter():

    def __init__(self):
        self.returni = 0
        self.asm = ''
        self.num_label = -1
        self.stcounter = 0
        self.stp = 16
        self.arithmetic = {'add': "D+M", 'sub':'M-D', 'or':'M|D', 'and':'D&M', 'eq':'JEQ', 'gt':'JLT','lt':'JGT', 'not':'!M', 'neg':'-M'}
    
    def create_file(self, filename):
        title = filename.split('/')[-2]
        if filename[-2:] == 'vm':
            file_out = open(filename[:-3]+'.asm','w')    
        else:
            file_out = open(filename+title+'.asm','w')
        self.close()
        file_out.write(self.asm)
        
    def set_static(self):
        self.stp += self.stcounter

    def write_arithmetic(self, command):
        self.num_label += 1
        if command == "add" or command == "sub" or command == "and" or command == "or":
            self.asm += "@SP\nAM=M-1\nD=M\nA=A-1\nM={0}\n".format(self.arithmetic[command]).replace('ENDIF','ENDIF{0}'.format(self.num_label)).replace('TRUE','TRUE{0}'.format(self.num_label))
        elif command == "gt" or command == "lt" or command == "eq":
            self.asm += "@SP\nAM=M-1\nD=M\n@SP\nAM=M-1\nD=D-M\n@TRUE\nD;{0}\n@SP\nA=M\nM=0\n@SP\nM=M+1\n@ENDIF\n0;JMP\n(TRUE)\n@SP\nA=M\nM=-1\n@SP\nM=M+1\n(ENDIF)\n".format(self.arithmetic[command]).replace('ENDIF','ENDIF{0}'.format(self.num_label)).replace('TRUE','TRUE{0}'.format(self.num_label))
        elif command == 'not' or command == 'neg':
            self.asm += "@SP\nAM=M-1\nM={0}\n@SP\nM=M+1\n".format(self.arithmetic[command])
        
    def write_init(self):
        self.asm += '@256\nD=A\n@SP\nM=D\n'#SP = 256
        self.write_call('Sys.init', '0')#call Sys.init
    
    def write_label(self, label):
        self.asm += "({0})\n".format(label)
    
    def write_goto(self, label):
        self.asm += "@{0}\n0;JMP\n".format(label)
    
    def write_if(self, label):
        self.asm += "@SP\nAM=M-1\nD=M\n"+"@{0}\nD;JNE\n".format(label)
    
    def write_call(self, function_name, num_args):
        self.asm += '@{0}\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'.format('return'+str(self.returni))#Save returnInstruction
        self.asm += '@LCL\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n' #Saves LCL
        self.asm += '@ARG\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n' #Saves ARG
        self.asm += '@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n' #Saves THIS
        self.asm += '@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n' #Saves THAT
        self.asm += '@SP\nD=M\n@{0}\nD=D-A\n@ARG\nM=D\n'.format(int(num_args)+5) #ARG = SP - nArg - 5
        self.asm += '@SP\nD=M\n@LCL\nM=D\n'#LCL = SP
        self.write_goto(function_name)#goto g
        self.write_label('return{0}'.format(self.returni))
        self.returni += 1

    def write_return(self):
        self.asm +='@LCL\nA=M\nD=A\n@R15\nM=D\n' #frame = LCL
        self.asm += '@R15\nD=M\n@5\nD=D-A\nA=D\nD=M\n@R14\nM=D\n'#retAddr = *(frame-5)
        self.asm += '@SP\nAM=M-1\nD=M\n@ARG\nA=M\nM=D\n'# *ARG = pop
        self.asm += '@ARG\nD=M\n@1\nD=D+A\n@SP\nM=D\n'#SP=ARG+1
        self.asm += '@R15\nD=M\n@1\nD=D-A\nA=D\nD=M\n@THAT\nM=D\n'#THAT=*(frame-1)
        self.asm += '@R15\nD=M\n@2\nD=D-A\nA=D\nD=M\n@THIS\nM=D\n'#THIS=*(frame-2)
        self.asm += '@R15\nD=M\n@3\nD=D-A\nA=D\nD=M\n@ARG\nM=D\n'#ARG=*(frame-3)
        self.asm += '@R15\nD=M\n@4\nD=D-A\nA=D\nD=M\n@LCL\nM=D\n'#LCL=*(frame-4)
        self.asm += '@R14\nA=M\n0;JMP\n'

    def write_function(self, function_name, num_locals):
        self.write_label(function_name)
        for i in range(num_locals):
            self.write_push_pop('push', 'constant', '0')

    def write_push_pop(self, command, segment, index):
        if command == 'push':
            if segment == 'constant':
                self.asm += "@{0}\n".format(index)+"D=A\n@SP\nA=M\nM=D\nD=A+1\n@SP\nM=D\n"
            elif segment == 'argument':
                self.asm += "@{0}\n".format(index)+'D=A\n@ARG\nA=M+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'
            elif segment == 'local':
                self.asm += "@{0}\n".format(index)+'D=A\n@LCL\nA=M+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'
            elif segment == 'this':
                self.asm += "@{0}\n".format(index)+'D=A\n@THIS\nA=M+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'
            elif segment == 'that':
                self.asm += "@{0}\n".format(index)+'D=A\n@THAT\nA=M+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'
            elif segment == 'pointer':
                if index == '0':
                    self.asm += '@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'
                elif index == '1':
                    self.asm += '@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'

            elif segment == 'static':
                if self.stp < 140: #if  (static + index) > 255 : Error
                    self.asm += "@{0}\n".format(index)+'D=A\n@{0}\nA=A+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'.format(self.stp)
                else:
                    print("INDEX ERROR: static " + index)
            elif segment == 'temp':
                if int(index) < 8:
                    self.asm += "@{0}\n".format(index)+'D=A\n@5\nA=A+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n'
                else:
                    print("INDEX ERROR: temp " + index)
                    return
            
        elif command == 'pop':
            if segment == 'static':
                self.stcounter += 1
                if int(index) < 140:
                    self.asm += '@{0}\nD=A\n@'.format(self.stp)+index+'\nD=D+A\n@R15\nM=D\n@SP\nAM=M-1\nD=M\n@R15\nA=M\nM=D\n'
                else:
                    print("INDEX ERROR: static " + index)
                    return
            elif segment == 'argument':
                self.asm += '@ARG\nD=M\n@'+index+'\nD=D+A\n@R15\nM=D\n@SP\nAM=M-1\nD=M\n@R15\nA=M\nM=D\n'
            elif segment == 'temp':
                if int(index) < 8:
                    self.asm += '@5\nD=A\n@'+index+'\nD=D+A\n@R15\nM=D\n@SP\nAM=M-1\nD=M\n@R15\nA=M\nM=D\n'
                else:
                    print("INDEX ERROR: temp " + index)
            elif segment == 'local':
                self.asm += '@LCL\nD=M\n@'+index+'\nD=D+A\n@R15\nM=D\n@SP\nAM=M-1\nD=M\n@R15\nA=M\nM=D\n'
            elif segment == 'that':
                self.asm += '@THAT\nD=M\n@'+index+'\nD=D+A\n@R15\nM=D\n@SP\nAM=M-1\nD=M\n@R15\nA=M\nM=D\n'
            elif segment == 'this':
                self.asm += '@THIS\nD=M\n@'+index+'\nD=D+A\n@R15\nM=D\n@SP\nAM=M-1\nD=M\n@R15\nA=M\nM=D\n'
            elif segment == 'pointer':
                if index == '0':
                    self.asm += '@SP\nAM=M-1\nD=M\n@THIS\nM=D\n'
                elif index == '1':
                    self.asm += '@SP\nAM=M-1\nD=M\n@THAT\nM=D\n'
    
    def close(self):
        self.asm += "(END)\n@END\n0;JMP\n"