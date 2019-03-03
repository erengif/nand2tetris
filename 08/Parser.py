import re

class Parser():    
    def __init__(self, file_name):
        self.input_file = ''
        self.current_inst = ''
        self.i = -1
        with open(file_name) as f:
            self.input_file = list(f)
            self.current_inst = self.input_file[0].replace(' ', '').replace('/n', '')
            f.closed

    def has_more_commands(self):
        return len(self.input_file) > self.i + 1
    
    def advance(self):
        self.i += 1
        self.current_inst = self.input_file[self.i].replace('\n', '')
        comments_pattern = r'//.*'
        self.current_inst=re.sub(comments_pattern, '',self.current_inst)
        self.current_inst = self.current_inst.split()
        if (len(self.current_inst) < 1):
            self.advance()  
        else:
            return

    def command_type(self):
        return self.current_inst[0]
        
    def arg1(self):
        return self.current_inst[1]
    
    def arg2(self):
        return self.current_inst[2]