package server;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public final class DatabaseManagerServlet extends HttpServlet {
	private static Boolean isActive;
	private static Connection connection;

	@Override
	public final void init(ServletConfig config) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant", "root", "");
			isActive = true;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			final PrintWriter writer = response.getWriter();
			
			if (!isActive) {
				writer.println("false");
				writer.println("Database connection is unavailable. Please try again later.");
				System.out.println("Database connection is unavailable. Please try again later.");
			} else {
				final String action = request.getParameter("action");

				if (action == null || action.isEmpty()) {
					writer.println("false");
					writer.println("No action is defined.");
					System.out.println("No action is defined.");
				} else {
					try {
						final Method method = DatabaseManagerServlet.class.getMethod(action, HttpServletRequest.class, PrintWriter.class);
						
						try {
							method.invoke(null, request, writer);
						} catch (Exception ex) {
							writer.println("false");
							writer.println(ex.getMessage());
							System.out.println(ex.getMessage() == null ? "NullPoinerException" : ex.getMessage());
							for (StackTraceElement item : ex.getStackTrace()) {
								System.out.println(item);
							}
						}
					} catch (Exception ex) {
						writer.println("false");
						writer.println("Unknown action is defined.");
						System.out.println("Unknown action is defined.");
					}
				}
			}

			writer.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	/**
	 * Performs a query on the database
	 * @param query The query string.
	 */
	private static final ResultSet query(String query)
	{
		try {
			final Statement stm = connection.createStatement();
			
			if (stm.execute(query))
				return stm.getResultSet();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Prepare an SQL statement for execution.
	 * @param query The query, as a string.
	 * @param params The number of variables and length of string types must match the parameters in the statement.
	 */
	private static final ResultSet prepare(String query, Object... params)
	{
		try {
			final PreparedStatement preparedStatement = connection.prepareStatement(query);

			for (int i = 0; i < params.length; i++)
				preparedStatement.setObject(i + 1, params[i]);
			
			if (preparedStatement.execute())
				return preparedStatement.getResultSet();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		return null;
	}

	public static final void getMenuCategoryList(HttpServletRequest request, PrintWriter writer) throws Exception {
		final ResultSet result = query("select * from menu_category order by Name");

		writer.println("true");
		
		while (result.next()) {
			writer.println(result.getString(1));
			writer.println(result.getString(2));
			writer.println(result.getString(3));
		}
		
		result.close();
	}
	
	public static final void setMenuCategoryList(HttpServletRequest request, PrintWriter writer) throws Exception {
		final int id = Integer.valueOf(request.getParameter("id"));
		final String name = request.getParameter("name");
		final Boolean isActive = Boolean.valueOf(request.getParameter("isActive"));
		
		final ResultSet result = prepare("select * from menu_category where Name = ?", name);
		int fetchCount = 0;
		
		while (result.next()) {
			fetchCount++;
		}
		
		result.close();
		
		if (fetchCount > 0) {
			writer.println("false");
			writer.println("Category name exists. Please try again.");
		} else {
			if (id == 0)
				prepare("insert into menu_category (Name, IsActive) values (?, ?)", name, isActive);
			else
				prepare("update menu_category set Name = ?, IsActive = ? where ID = ?", name, isActive, id);
			
			writer.println("true");
		}
	}
	
	public static final void deleteMenuCategoryList(HttpServletRequest request, PrintWriter writer) throws Exception {
		final int id = Integer.valueOf(request.getParameter("id"));
		
		prepare("delete from menu_category where ID = ?", id);
		
		writer.println("true");
	}
	
	public static final void getMenuList(HttpServletRequest request, PrintWriter writer) throws Exception {
		final ResultSet result = query("select menu.ID, menu.Name, menu_category.ID, menu_category.Name, menu.Price, menu.Image, menu.IsActive from menu, menu_category where menu.CategoryID = menu_category.ID order by menu_category.Name, menu.Name");

		writer.println("true");
		
		while (result.next()) {
			writer.println(result.getString(1));
			writer.println(result.getString(2));
			writer.println(result.getString(3));
			writer.println(result.getString(4));
			writer.println(result.getString(5));
			writer.println(result.getString(6) == null ? "NULL" : result.getString(6));
			writer.println(result.getString(7));
		}
		
		result.close();
	}
	
	public static final void setMenuList(HttpServletRequest request, PrintWriter writer) throws Exception {
		final int id = Integer.valueOf(request.getParameter("id"));
		final String name = request.getParameter("name");
		final int categoryID = Integer.valueOf(request.getParameter("categoryID"));
		final double price = Double.valueOf(request.getParameter("price"));
		final String image = request.getParameter("image");
		final Boolean isActive = Boolean.valueOf(request.getParameter("isActive"));
		
		final ResultSet result = prepare("select * from menu where Name = ?", name);
		int fetchCount = 0;
		
		while (result.next()) {
			fetchCount++;
		}
		
		result.close();
		
		if (fetchCount > 0) {
			writer.println("false");
			writer.println("Menu name exists. Please try again.");
		} else {
			if (id == 0)
				prepare("insert into menu (Name, CategoryID, Price, Image, IsActive) values (?, ?, ?, ?, ?)", name, categoryID, price, image.contentEquals("NULL") ? null : image, isActive);
			else
				prepare("update menu set Name = ?, CategoryID = ?, Price = ?, Image = ?, IsActive = ? where ID = ?", name, categoryID, price, image, isActive, id);
			
			writer.println("true");
		}
	}
	
	public static final void deleteMenuList(HttpServletRequest request, PrintWriter writer) throws Exception {
		final int id = Integer.valueOf(request.getParameter("id"));
		
		prepare("delete from menu where ID = ?", id);
		
		writer.println("true");
	}
	
	public static final void getAccount(HttpServletRequest request, PrintWriter writer) throws Exception {
		final String name = request.getParameter("name");
		final String password = request.getParameter("password");
		
		final ResultSet result = prepare("select * from account where Username = ? and Password = ?", name, password);
		int fetchCount = 0;
		
		while (result.next()) {
			fetchCount++;
		}
		
		if (fetchCount > 0) {
			result.first();
			writer.println("true");
			writer.println(result.getInt(1));
			writer.println(result.getInt(4));
			writer.println(result.getInt(5));
		} else {
			writer.println("false");
			writer.println("Invalid username or password. Please try again!");
		}
	}
	
	public static final void setAccount(HttpServletRequest request, PrintWriter writer) throws Exception {
		final String username = request.getParameter("username");
		final String password = request.getParameter("password");
		
		final ResultSet result = prepare("select * from account where Username = ?", username);
		int fetchCount = 0;
		
		while (result.next()) {
			fetchCount++;
		}
		
		result.close();
		
		if (fetchCount == 0) {
			prepare("insert into account (Username, Password, IsAdmin, IsBanned) values (?, ?, ?, ?)", username, password, 0, 0);
			
			writer.println("true");
		} else {
			writer.println("false");
			writer.println("There is an existing username. Please try another name.");
		}
	}
	
	public static final void setOrder(HttpServletRequest request, PrintWriter writer) throws Exception {
		final int accountID = Integer.valueOf(request.getParameter("accountID"));
		final int menuID = Integer.valueOf(request.getParameter("menuID"));
		final int quantity = Integer.valueOf(request.getParameter("quantity"));
		final String additionalRequest = request.getParameter("additionalRequest");
		final long dateTime = Long.valueOf(request.getParameter("dateTime"));
		
		prepare("insert into `order` (AccountID, MenuID, Quantity, AdditionalRequest, DateTime) values (?, ?, ?, ?, ?)", accountID, menuID, quantity, additionalRequest.contentEquals("NULL") ? null : additionalRequest, dateTime);
		
		writer.println("true");
	}
}
