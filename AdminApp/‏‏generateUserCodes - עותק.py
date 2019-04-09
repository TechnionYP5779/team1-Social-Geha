from tkinter import *
from tkinter.filedialog import askopenfilename
from tkinter.messagebox import showerror
from tkinter import ttk
import csv
import hashlib
import base64
import struct

"""
This script takes in a CSV file of the following row format:

ID NUMBER, NAME, ...

Then generates a new file with the codes in the directory of the original data file.
"""

def fileIsValid(path):
    with open(path,'r') as file:
        try:
            dialect = csv.Sniffer().sniff(file.read(1024))
        except csv.Error:
            return False
    return True
        

def fnv64(data):
    hash_ = 0xcbf29ce484222325
    for b in data:
        hash_ *= 0x100000001b3
        hash_ &= 0xffffffffffffffff
        hash_ ^= b
    return hash_

def hash_dn(dn, salt):
    # Turn dn into bytes with a salt, dn is expected to be ascii data
    data = salt.encode("ascii") + dn.encode("ascii")
    # Hash data
    hash_ = fnv64(data)
    # Pack hash (int) into bytes
    bhash = struct.pack("<Q", hash_)
    # Encode in base64. There is always a padding "=" at the end, because the
    # hash is always 64bits long. We don't need it.
    return base64.urlsafe_b64encode(bhash)[:-1].decode("ascii")



class MyFrame(Frame):
    def __init__(self):
        Frame.__init__(self)
        self.fname = None
        self.master.title("Example")
        self.master.rowconfigure(5, weight=1)
        self.master.columnconfigure(2, weight=1)
        self.grid(sticky=W+E+N+S)
        self.label1 = Label(self,text='Load data file:',height=0,width=20)
        self.label1.grid(row=1, column=0, sticky=W)
        
        
        self.browsebutton = Button(self, text="Browse", command=self.load_file, width=10)
        self.browsebutton.grid(row=1, column=1, sticky=S)
        
        self.label1b = Label(self,text='File not loaded',height=0, bg="red")
        self.label1b.grid(row=1, column=2, sticky=W)
        
        
        self.label2 = Label(self,text='Select the data file:',height=0,width=20)
        self.label2.grid(row=3, column=0, sticky=W)
        
        self.generatebutton = Button(self, text="Generate", command=self.generate_keys, width=10)
        self.generatebutton.grid(row=3, column=1, sticky=S)
        
        ttk.Separator(self,orient=HORIZONTAL).grid(row=5, columnspan=5, sticky="ew")
        
        self.labeldone = Label(self,text='',height=0)
        self.labeldone.grid(row=7, column=0, sticky=S)
        
        ttk.Separator(self,orient=HORIZONTAL).grid(row=8, columnspan=5, sticky="ew")

        self.label3 = Label(self,text='Cool:',height=0)
        self.label3.grid(row=9, column=0, sticky=W)
        self.entrythingy = Entry(self,text='cake')
        self.entrythingy.grid(row=9, column=1, sticky=W)

        # here is the application variable
        self.contents = StringVar()
        # set it to some value
        self.contents.set("this is a variable")
        # tell the entry widget to watch this variable
        self.entrythingy["textvariable"] = self.contents

        # and here we get a callback when the user hits return.
        # we will have the program print out the value of the
        # application variable when the user hits return
        self.entrythingy.bind('<Key-Return>',
                              self.print_contents)

    def print_contents(self, event):
        print("hi. contents of entry is now ---->",
              self.contents.get())

    def load_file(self):
        fname = askopenfilename(filetypes=(("CSV files", "*.csv"),
                                           ("All files", "*.*") ))
        
        if fname:
            if fileIsValid(fname):
                self.fname = fname
                self.label1b['text'] = 'File loaded successfully'
                self.label1b['bg'] = 'green'
                return fname
            else:
                self.label1b['text'] = 'File not loaded'
                self.label1b['bg'] = 'red'
                showerror("Open Source File", "File is not of CSV format\n'%s'" % fname)
        else:
            self.label1b['text'] = 'File not loaded'
            self.label1b['bg'] = 'red'
            showerror("Open Source File", "Failed to read file\n'%s'" % fname)
            return None

    def create_code(self,row):
        return hash_dn(row[0]+row[1],'1')
    
    def myhash(self,s):
        return binascii.b2a_base64(struct.pack('i', hash(s))).decode('utf-8', 'ignore')[:-1]        

        
    def generate_keys(self):
        if self.fname == None:
            showerror("Generate Keys File", "Please select a file first.\n'%s'" % self.fname)
            return
        else:
            split_path = self.fname.split('.') 
            output_path = split_path[0] + 'out.' + split_path[1]
            with open(self.fname,'r') as data_file:
                with open(output_path,'w',newline='') as output_file:
                    reader = csv.reader(data_file,delimiter=',', quotechar='|')
                    writer = csv.writer(output_file,delimiter=',', quotechar='|')
                    for row in reader:
                        code = self.create_code(row)
                        writer.writerow(row+[code])
            self.labeldone['text'] = 'Generated output file: %s' % output_path
        return            
        
        

if __name__ == "__main__":
    MyFrame().mainloop()