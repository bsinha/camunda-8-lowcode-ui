import React, { useState, useEffect } from 'react';
import { IInstanceViewer } from '../store/model';
import api from '../service/api';
import { Form, InputGroup, Table, Button } from 'react-bootstrap';
import authService from '../service/AuthService';


function InstanceComments(props: IInstanceViewer) {
  const [comments, setComments] = useState<any[]>([]);
  const [isPublic, setIsPublic] = useState<boolean>(true);

  useEffect(() => {
    if (props && props.instancekey) {
      loadComments();
    }
  }, [props.instancekey]);

  const submitComment = () => {
    let value = (document.getElementById("newComment") as HTMLInputElement)!.value;
    api.post('/comments/' + props.instancekey, { "content": value , "isPublic": isPublic }).then((response: any) => {
      setComments(response.data);
    }).catch((error: any) => {
      alert(error.message);
    })
  }

  const loadComments = () => {
    api.get('/comments/' + props.instancekey).then((response: any) => {
      setComments(response.data);
    }).catch((error: any) => {
      alert(error.message);
    })
  }

  const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsPublic(event.target.checked); // Update state on checkbox change
  };

  return (
    <>
      <InputGroup>
        <InputGroup.Text>New comment</InputGroup.Text>
        <Form.Control id="newComment" as="textarea" />
        
        <Form.Check 
          type="checkbox"
          id="publicCheckbox"
          label="Mark as Public" 
          checked={isPublic}
          onChange={handleCheckboxChange}
          className="mx-2"
        />

        <Button variant="primary" onClick={submitComment}> <i className="bi bi-send"></i> Send</Button>
      </InputGroup>
      <hr />
      <Table striped hover variant="light">
        <thead >
          <tr >
            <th className="bg-primary text-light">Author</th>
            <th className="bg-primary text-light">Comment</th>
            <th className="bg-primary text-light">Date</th>
          </tr>
        </thead>
        <tbody>
        {comments.map((comment: any, index: number) => {
          const loggedInUser = authService.getUser()?.username; // Safely get logged-in username
          const isAuthor = loggedInUser === comment.author; // Check if logged-in user is the author
          const isPublic = comment.isPublic; // Check if comment is public
          let displayComment;

          // Check if the logged-in user is the author or the comment is public
          if (loggedInUser === comment.author) {
            displayComment = comment.comment; // Show the comment if the user is the author
          } else if (isPublic === "false") {
            displayComment = "xxxxx"; // Show the comment if it's public
          } else {
            displayComment = comment.comment; // Mask the comment if neither condition is true
          }
            return (
              <tr key={index}>
                <td>{comment.author}</td>
                <td>
                  {displayComment}
                </td>
                <td>{comment.date}</td>
              </tr>
            );
          })}
        </tbody>
      </Table>
    </>
  );
}

export default InstanceComments;
