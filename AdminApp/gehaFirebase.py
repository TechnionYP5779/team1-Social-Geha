import firebase_admin
import google
from firebase_admin import credentials, firestore


class GehaFirebase(object):
    def __init__(self,pKeyPath):
        self.cred = credentials.Certificate(pKeyPath)
        self.default_app = firebase_admin.initialize_app(self.cred)
        self.db = firestore.client()
        
    def addUser(self,user):
        self.db.collection(u'users').document(user['id']).set(user)
        
    def getUser(self,id):
        doc_ref = self.db.collection(u'users').document(id)
        try:
            doc = doc_ref.get()
            print(u'Document data: {}'.format(doc.to_dict()))
            return doc.to_dict()
        except google.cloud.exceptions.NotFound:
            print(u'No such document!')
        return None
    

    
example_user = {
    u'fullname': u'Itamar Ordani2',
    u'id' : u'318301273',
    u'usercode' : u'12345678'
}

#firegeha = GehaFirebase("../../socialGehaPrivateKey.json")
#firegeha.getUser(example_user['id'])
