package edu.eci.pdsw.managedBeans;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import edu.eci.pdsw.entities.User;
import edu.eci.pdsw.entities.UserType;
import edu.eci.pdsw.samples.services.InitiativeServices;
import edu.eci.pdsw.samples.services.ServicesException;



@SuppressWarnings("deprecation")
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean extends BasePageBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3594009161252782831L;
	
	
	@Inject
	InitiativeServices initiativeService;
	
	public void logIn(String email, String password) throws ServicesException, IOException {
		User user = initiativeService.getUser(email, password);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if(user != null) {
			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
			session.setAttribute("id", user.getId());
			session.setAttribute("name", user.getName());
			session.setAttribute("type", user.getType().ordinal());
			facesContext.getExternalContext().redirect("/faces/initiativeKeyword.xhtml");
		}
		else {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Usuario o clave invalido","Error"));
		}
	}
	
	public void logOut() throws ServicesException, IOException{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		session.removeAttribute("id");
		facesContext.getExternalContext().redirect("/faces/index.xhtml");
	} 
	
	public boolean islogged() {
		return ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).getAttribute("id") != null;
	}
	
	public boolean isAdmin() {
		if(islogged()) {
			HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			return sesion.getAttribute("type").toString().equals("0") || sesion.getAttribute("type").toString().equals("1");
		}
		return false;
	}
	
	public boolean isPublic() {
		if(islogged()) {
			HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			return sesion.getAttribute("type").toString().equals("3");
		}
		return false;
	}
	
	public boolean hasVoted(int idIni) throws ServicesException {
		return initiativeService.hasVoted(idIni, Integer.parseInt(((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).getAttribute("id").toString()));
	}
	
	public String getName() {
		return ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).getAttribute("name").toString();
	}
		
	public List<User> getUsers() throws Exception{
		try {
			return initiativeService.listUsers();
		}
		catch(ServicesException ex) {
			throw ex;
		}
	}
	
	public List<User> getUserByMail(String usermail) throws Exception{
		try {
			return initiativeService.consultUserByMail(usermail);
		}
		catch(ServicesException ex) {
			throw ex;
		}
	}
	
	public List<UserType> getTypes (){
		return Arrays.asList(UserType.class.getEnumConstants() );
	}
	
	public void modifyUser(String email, int rol) {
		try {
			initiativeService.modifyUser(email, rol);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Datos Guardados"));
			
		} catch (ServicesException e) {
			// TODO Auto-generated catch block
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No se pudo guardar","Error"));
			e.printStackTrace();
		}
	}
	
	
}